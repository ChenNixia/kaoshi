import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询用户列表
export function listExamRealQuestions(query) {
  return request({
    url: '/exam/examRealQuestions/list',
    method: 'get',
    params: query
  })
}

// 新增用户
export function saveExamRealQuestions(data) {
  return request({
    url: '/exam/examRealQuestions/save',
    method: 'post',
    data: data
  })
}


// 删除用户
export function delExamRealQuestions(id) {
  return request({
    url: '/exam/examRealQuestions/del/' + id,
    method: 'post'
  })
}

export function generateQuestions(id) {
  return request({
    url: '/exam/examRealQuestions/generateQuestions/' + id,
    method: 'post'
  })
}

export function sel(id) {
  return request({
    url: '/exam/examRealQuestions/sel/' + id,
    method: 'post'
  })
}

