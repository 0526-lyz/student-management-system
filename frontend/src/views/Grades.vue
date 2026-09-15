<template>
  <div class="page-card">
    <div class="page-toolbar">
      <el-input
        v-model="query.studentNo"
        placeholder="学号"
        clearable
        style="width: 160px"
        :prefix-icon="Search"
        @keyup.enter="load"
        @clear="load"
      />
      <el-input
        v-model="query.course"
        placeholder="课程名称"
        clearable
        style="width: 180px"
        :prefix-icon="Search"
        @keyup.enter="load"
        @clear="load"
      />
      <el-button type="primary" :icon="Search" @click="load">搜索</el-button>
      <el-button @click="reset">重置</el-button>
      <div class="spacer" />
      <el-button type="primary" :icon="Plus" @click="openDialog()">录入成绩</el-button>
    </div>

    <div class="table-wrap">
      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="studentNo" label="学号" min-width="110" />
        <el-table-column prop="studentName" label="姓名" min-width="90" />
        <el-table-column prop="course" label="课程" min-width="140" />
        <el-table-column prop="score" label="分数" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score < 60 ? 'danger' : 'success'" effect="light">
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" min-width="120" />
        <el-table-column prop="createTime" label="录入时间" min-width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.gradeId ? '编辑成绩' : '录入成绩'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学生学号" />
        </el-form-item>
        <el-form-item label="课程" prop="course">
          <el-input v-model="form.course" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-input-number
            v-model="form.score"
            :min="0"
            :max="100"
            :precision="1"
            :step="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="form.semester" placeholder="如 2024-2025-1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { gradeApi } from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()
const query = reactive({ studentNo: '', course: '' })

const emptyForm = () => ({
  gradeId: null,
  studentNo: '',
  course: '',
  score: 0,
  semester: ''
})

const form = reactive(emptyForm())

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  course: [{ required: true, message: '请输入课程', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分数', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const hasQuery = query.studentNo || query.course
    list.value = hasQuery ? await gradeApi.search(query) : await gradeApi.list()
  } finally {
    loading.value = false
  }
}

function reset() {
  query.studentNo = ''
  query.course = ''
  load()
}

function openDialog(row) {
  Object.assign(form, emptyForm(), row || {})
  dialogVisible.value = true
}

async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.gradeId) {
      await gradeApi.update(form.gradeId, form)
      ElMessage.success('修改成功')
    } else {
      await gradeApi.add(form)
      ElMessage.success('录入成功')
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除该条成绩记录吗？`, '提示', { type: 'warning' })
  try {
    await gradeApi.remove(row.gradeId)
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

onMounted(load)
</script>
