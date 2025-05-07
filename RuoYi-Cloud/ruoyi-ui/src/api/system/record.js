import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";


// 新增用户
export function submitRecord(realId,data) {
  return request({
    url: '/evaluation/record/submit/'+realId,
    method: 'post',
    data: data
  })
}

export function selRecord() {
  return request({
    url: '/evaluation/record/sel',
    method: 'post',
  })
}

export function findAll(id) {
  return request({
    url: '/evaluation/record/findAll/'+id,
    method: 'post',
  })
}

export function findUser() {
  return request({
    url: '/evaluation/record/findUser',
    method: 'post',
  })
}


