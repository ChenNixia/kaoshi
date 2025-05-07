import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询用户列表
export function listQuestion(query) {
  return request({
    url: '/exam/question/getAllQuestions',
    method: 'get',
    params: query
  })
}

// 新增用户
export function saveQuestion(data) {
  return request({
    url: '/exam/question/save',
    method: 'post',
    data: data
  })
}


// 删除用户
export function delQuestion(id) {
  return request({
    url: '/exam/question/del/' + id,
    method: 'post'
  })
}

