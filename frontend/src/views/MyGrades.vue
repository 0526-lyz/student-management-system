<template>
  <div>
    <div class="page-card summary">
      <div class="summary-item">
        <div class="summary-label">课程门数</div>
        <div class="summary-value">{{ list.length }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">平均分</div>
        <div class="summary-value">{{ avgScore }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">最高分</div>
        <div class="summary-value">{{ maxScore }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">最低分</div>
        <div class="summary-value">{{ minScore }}</div>
      </div>
    </div>

    <div class="page-card">
      <div class="page-toolbar">
        <el-input
          v-model="course"
          placeholder="按课程名称筛选"
          clearable
          style="width: 220px"
          :prefix-icon="Search"
        />
      </div>
      <el-table :data="filtered" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="course" label="课程" min-width="160" />
        <el-table-column prop="score" label="分数" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score < 60 ? 'danger' : 'success'" effect="light">
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" min-width="140" />
        <el-table-column prop="createTime" label="录入时间" min-width="180" />
        <el-table-column label="评级" width="100" align="center">
          <template #default="{ row }">{{ level(row.score) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { gradeApi } from '../api'
import { user } from '../store/auth'

const list = ref([])
const loading = ref(false)
const course = ref('')

const filtered = computed(() => {
  if (!course.value) return list.value
  return list.value.filter((g) => (g.course || '').includes(course.value))
})

const avgScore = computed(() => {
  if (!list.value.length) return '--'
  return (list.value.reduce((s, g) => s + g.score, 0) / list.value.length).toFixed(1)
})
const maxScore = computed(() => (list.value.length ? Math.max(...list.value.map((g) => g.score)) : '--'))
const minScore = computed(() => (list.value.length ? Math.min(...list.value.map((g) => g.score)) : '--'))

function level(score) {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '中等'
  if (score >= 60) return '及格'
  return '不及格'
}

onMounted(async () => {
  loading.value = true
  try {
    list.value = await gradeApi.search({ studentNo: user.value?.username })
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-item {
  padding: 8px 4px;
}

.summary-label {
  color: #9aa1b0;
  font-size: 13px;
}

.summary-value {
  font-size: 28px;
  font-weight: 800;
  color: #2b2f3a;
  margin-top: 4px;
}
</style>
