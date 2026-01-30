import request from '@/utils/http'

export function list(query) {
  return request({
    url: '/tongzhijilu/page',
    method: 'get',
    params: query
  })
}

export function getDetail(id) {
  return request({
    url: '/tongzhijilu/info/' + id,
    method: 'get'
  })
}

export function create(data) {
  return request({
    url: '/tongzhijilu/save',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/tongzhijilu/update',
    method: 'put',
    data
  })
}

export function del(id) {
  return request({
    url: '/tongzhijilu/delete',
    method: 'delete',
    data: [id]
  })
}

export function retryNotification(id) {
  return request({
    url: '/tongzhijilu/retry/' + id,
    method: 'post'
  })
}

export function handleFailedNotification(id, chulizhuangtai, beizhixinxi) {
  return request({
    url: '/tongzhijilu/handle/' + id,
    method: 'post',
    params: {
      chulizhuangtai,
      beizhixinxi
    }
  })
}

export function batchRetry(ids) {
  return request({
    url: '/tongzhijilu/batchRetry',
    method: 'post',
    data: {
      ids
    }
  })
}

export function batchHandle(ids, chulizhuangtai, beizhixinxi) {
  return request({
    url: '/tongzhijilu/batchHandle',
    method: 'post',
    params: {
      ids,
      chulizhuangtai,
      beizhixinxi
    }
  })
}