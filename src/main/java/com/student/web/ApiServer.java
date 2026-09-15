package com.student.web;

import com.google.gson.JsonObject;
import com.student.AppContext;
import com.student.model.Grade;
import com.student.model.Notification;
import com.student.model.Student;
import com.student.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 基于 JDK 内置 HttpServer 的轻量 REST API，为 Vue 前端提供 JSON 接口。
 * 复用现有 Service 层，无需引入 Spring 等重型框架。
 *
 * 统一响应格式：成功 {@code {code:0, message:"success", data:...}}，
 * 失败 {@code {code:1, message:"..."}}。
 */
public class ApiServer {

    private static final String STATIC_ROOT = "frontend/dist";

    private final AppContext context;

    public ApiServer() {
        this.context = new AppContext();
        // 与桌面端保持一致：成绩录入/更新时自动触发邮件与站内信通知
        this.context.getGradeService().registerObserver(new com.student.observer.EmailNotifier());
        this.context.getGradeService().registerObserver(new com.student.observer.MessageNotifier(context.getNotificationService()));
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new Handler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("REST API 已启动: http://localhost:" + port + "/api");
    }

    private class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String method = ex.getRequestMethod();
            String path = ex.getRequestURI().getPath();
            try {
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    cors(ex);
                    ex.sendResponseHeaders(204, -1);
                    return;
                }
                String[] seg = path.split("/");
                // seg = ["", "api", resource, ...]
                if (seg.length >= 3 && "api".equals(seg[1])) {
                    switch (seg[2]) {
                        case "health" -> respond(ex, ok(Map.of("status", "ok")));
                        case "auth" -> handleAuth(ex, method, seg);
                        case "students" -> handleStudents(ex, method, seg);
                        case "grades" -> handleGrades(ex, method, seg);
                        case "notifications" -> handleNotifications(ex, method, seg);
                        case "users" -> handleUsers(ex, method, seg);
                        case "stats" -> handleStats(ex, method);
                        default -> respond(ex, 404, error("接口不存在"));
                    }
                } else {
                    handleStatic(ex);
                }
            } catch (Exception e) {
                e.printStackTrace();
                respond(ex, 500, error("服务器内部错误: " + e.getMessage()));
            }
        }
    }

    // ---------- 认证 ----------

    private void handleAuth(HttpExchange ex, String method, String[] seg) throws IOException {
        if (seg.length < 4) {
            respond(ex, 404, error("接口不存在"));
            return;
        }
        String action = seg[3];
        JsonObject body = readBody(ex);
        if ("login".equals(action) && "POST".equalsIgnoreCase(method)) {
            User user = context.getUserService().login(
                    Json.str(body, "username"), Json.str(body, "password"));
            if (user == null) {
                respond(ex, error("用户名或密码错误"));
            } else {
                respond(ex, ok(user));
            }
        } else if ("register".equals(action) && "POST".equalsIgnoreCase(method)) {
            boolean ok = context.getUserService().register(
                    Json.str(body, "username"),
                    Json.str(body, "password"),
                    Json.str(body, "role", "student"),
                    Json.str(body, "email"));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("用户名已存在"));
            }
        } else {
            respond(ex, 404, error("接口不存在"));
        }
    }

    // ---------- 学生 ----------

    private void handleStudents(HttpExchange ex, String method, String[] seg) throws IOException {
        if (seg.length == 3) {
            if ("GET".equalsIgnoreCase(method)) {
                respond(ex, ok(context.getStudentService().findAll()));
            } else if ("POST".equalsIgnoreCase(method)) {
                JsonObject b = readBody(ex);
                boolean ok = context.getStudentService().addStudent(studentFromBody(b, 0));
                if (ok) {
                    respond(ex, ok(true));
                } else {
                    respond(ex, error("学号已存在"));
                }
            } else {
                respond(ex, 405, error("不支持的请求方法"));
            }
            return;
        }

        if (seg.length == 4 && "search".equals(seg[3]) && "GET".equalsIgnoreCase(method)) {
            String keyword = query(ex, "keyword");
            respond(ex, ok(context.getStudentService().search(keyword == null ? "" : keyword)));
            return;
        }

        if (seg.length == 4 && "PUT".equalsIgnoreCase(method)) {
            int id = parseId(seg[3]);
            boolean ok = context.getStudentService().updateStudent(studentFromBody(readBody(ex), id));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("修改失败"));
            }
            return;
        }

        if (seg.length == 4 && "DELETE".equalsIgnoreCase(method)) {
            boolean ok = context.getStudentService().deleteStudent(parseId(seg[3]));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("删除失败"));
            }
            return;
        }

        respond(ex, 404, error("接口不存在"));
    }

    private Student studentFromBody(JsonObject b, int id) {
        Student s = new Student();
        s.setStudentId(id);
        s.setStudentNo(Json.str(b, "studentNo"));
        s.setName(Json.str(b, "name"));
        s.setGender(Json.str(b, "gender"));
        s.setClassName(Json.str(b, "className"));
        s.setMajor(Json.str(b, "major"));
        s.setPhone(Json.str(b, "phone"));
        s.setEmail(Json.str(b, "email"));
        return s;
    }

    // ---------- 成绩 ----------

    private void handleGrades(HttpExchange ex, String method, String[] seg) throws IOException {
        if (seg.length == 3) {
            if ("GET".equalsIgnoreCase(method)) {
                respond(ex, ok(context.getGradeService().findAll()));
            } else if ("POST".equalsIgnoreCase(method)) {
                boolean ok = context.getGradeService().addGrade(gradeFromBody(readBody(ex), 0));
                if (ok) {
                    respond(ex, ok(true));
                } else {
                    respond(ex, error("学生不存在"));
                }
            } else {
                respond(ex, 405, error("不支持的请求方法"));
            }
            return;
        }

        if (seg.length == 4 && "search".equals(seg[3]) && "GET".equalsIgnoreCase(method)) {
            List<Grade> list = context.getGradeService().search(query(ex, "studentNo"), query(ex, "course"));
            respond(ex, ok(list));
            return;
        }

        if (seg.length == 4 && "PUT".equalsIgnoreCase(method)) {
            boolean ok = context.getGradeService().updateGrade(gradeFromBody(readBody(ex), parseId(seg[3])));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("修改失败"));
            }
            return;
        }

        if (seg.length == 4 && "DELETE".equalsIgnoreCase(method)) {
            boolean ok = context.getGradeService().deleteGrade(parseId(seg[3]));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("删除失败"));
            }
            return;
        }

        respond(ex, 404, error("接口不存在"));
    }

    private Grade gradeFromBody(JsonObject b, int id) {
        Grade g = new Grade();
        g.setGradeId(id);
        g.setStudentNo(Json.str(b, "studentNo"));
        g.setCourse(Json.str(b, "course"));
        g.setScore(Json.decimal(b, "score", 0));
        g.setSemester(Json.str(b, "semester"));
        return g;
    }

    // ---------- 通知 ----------

    private void handleNotifications(HttpExchange ex, String method, String[] seg) throws IOException {
        if (seg.length == 3 && "GET".equalsIgnoreCase(method)) {
            String username = query(ex, "username");
            List<Notification> list = (username == null || username.isEmpty())
                    ? context.getNotificationService().findAll()
                    : context.getNotificationService().findByUsername(username);
            respond(ex, ok(list));
            return;
        }

        if (seg.length == 5 && "read".equals(seg[4]) && "POST".equalsIgnoreCase(method)) {
            boolean ok = context.getNotificationService().markAsRead(parseId(seg[3]));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("操作失败"));
            }
            return;
        }

        if (seg.length == 4 && "read-all".equals(seg[3]) && "POST".equalsIgnoreCase(method)) {
            boolean ok = context.getNotificationService().markAllRead(query(ex, "username"));
            if (ok) {
                respond(ex, ok(true));
            } else {
                respond(ex, error("操作失败"));
            }
            return;
        }

        respond(ex, 404, error("接口不存在"));
    }

    // ---------- 用户与统计 ----------

    private void handleUsers(HttpExchange ex, String method, String[] seg) throws IOException {
        if (seg.length == 3 && "GET".equalsIgnoreCase(method)) {
            respond(ex, ok(context.getUserService().findAll()));
        } else {
            respond(ex, 404, error("接口不存在"));
        }
    }

    private void handleStats(HttpExchange ex, String method) throws IOException {
        if (!"GET".equalsIgnoreCase(method)) {
            respond(ex, 405, error("不支持的请求方法"));
            return;
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("studentCount", context.getStudentService().findAll().size());
        stats.put("gradeCount", context.getGradeService().findAll().size());
        stats.put("notificationCount", context.getNotificationService().findAll().size());
        stats.put("userCount", context.getUserService().findAll().size());
        respond(ex, ok(stats));
    }

    // ---------- 静态资源 ----------

    private void handleStatic(HttpExchange ex) throws IOException {
        Path root = Paths.get(STATIC_ROOT).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            String msg = "前端尚未构建：请先在 frontend 目录执行 npm run build";
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(bytes);
            }
            return;
        }

        String path = ex.getRequestURI().getPath();
        Path file = root.resolve(path.startsWith("/") ? path.substring(1) : path).normalize();
        if (!file.startsWith(root)) {
            respondRaw(ex, 403, "{\"code\":1,\"message\":\"Forbidden\"}");
            return;
        }
        if ("/".equals(path) || !Files.isRegularFile(file)) {
            file = root.resolve("index.html");
        }

        byte[] bytes = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", mime(file.getFileName().toString()));
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private String mime(String name) {
        if (name.endsWith(".html")) return "text/html; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".json")) return "application/json; charset=utf-8";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".ico")) return "image/x-icon";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    // ---------- 通用工具 ----------

    private int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String query(HttpExchange ex, String key) {
        String raw = ex.getRequestURI().getRawQuery();
        if (raw == null) {
            return null;
        }
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private JsonObject readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody()) {
            String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            if (s == null || s.isBlank()) {
                return new JsonObject();
            }
            return Json.parse(s);
        }
    }

    private void respond(HttpExchange ex, Map<String, Object> body) throws IOException {
        respondRaw(ex, 200, Json.toJson(body));
    }

    private void respond(HttpExchange ex, int status, Map<String, Object> body) throws IOException {
        respondRaw(ex, status, Json.toJson(body));
    }

    private void respondRaw(HttpExchange ex, int status, String json) throws IOException {
        cors(ex);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private void cors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> m = new HashMap<>();
        m.put("code", 0);
        m.put("message", "success");
        m.put("data", data);
        return m;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("code", 1);
        m.put("message", message);
        return m;
    }
}
