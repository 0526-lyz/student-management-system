package com.student.web;

/**
 * REST API 入口：启动内置 HTTP 服务器，为 Vue 前端提供服务。
 *
 * 运行方式（在项目根目录）：
 *   mvn -q compile exec:java -Dexec.mainClass=com.student.web.WebServer
 * 或在 IDE 中直接运行本类的 main 方法。
 */
public class WebServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new ApiServer().start(port);
    }
}
