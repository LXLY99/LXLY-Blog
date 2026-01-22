# Apipost 测试脚本示例（情侣模块）

> 说明：以下为 Apipost 中「Tests」脚本示例与请求配置建议，可直接复制到对应接口的测试脚本区域。

## 0) 关系管理

### 发送邀请
**接口**：`POST /api/couple/invite`  
**Header**：`Authorization: Bearer {{token}}`  
**Body (JSON)**：
```json
{
  "targetUserId": 2
}
```
**Tests**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});
pm.test("response has relation id", function () {
  var json = pm.response.json();
  pm.expect(json.data).to.have.property("id");
});
```

### 接受邀请
**接口**：`POST /api/couple/accept`  
**Body (JSON)**：
```json
{
  "relationId": 1
}
```
**Tests**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});
pm.test("relation status is ACTIVE", function () {
  var json = pm.response.json();
  pm.expect(json.data).to.have.property("status", "ACTIVE");
});
```

### 拒绝邀请
**接口**：`POST /api/couple/reject`  
**Body (JSON)**：
```json
{
  "relationId": 1
}
```
**Tests**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});
```

### 解除关系
**接口**：`POST /api/couple/breakup`
**Tests**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});
```

### 查看关系状态
**接口**：`GET /api/couple/status`
**Tests**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});
pm.test("response has active and pendingInvites", function () {
  var json = pm.response.json();
  pm.expect(json.data).to.have.property("active");
  pm.expect(json.data).to.have.property("pendingInvites");
});
```

## 1) 情侣相册

### 创建相册
**接口**：`POST /api/couple/albums`  
**Body (JSON)**：
```json
{
  "name": "旅行相册",
  "coverUrl": "https://example.com/cover.jpg",
  "description": "2024年旅行"
}
```

### 获取相册列表
**接口**：`GET /api/couple/albums`

### 删除相册
**接口**：`DELETE /api/couple/albums/{id}`

## 2) 相册照片

### 添加照片
**接口**：`POST /api/couple/albums/photos`  
**Body (JSON)**：
```json
{
  "albumId": 1,
  "url": "https://example.com/photo.jpg",
  "note": "海边"
}
```

### 获取照片列表
**接口**：`GET /api/couple/albums/{albumId}/photos`

### 删除照片
**接口**：`DELETE /api/couple/albums/photos/{id}`

## 3) 共享日历

### 创建日程
**接口**：`POST /api/couple/calendar`  
**Body (JSON)**：
```json
{
  "title": "周年纪念",
  "description": "晚餐",
  "startTime": "2024-10-01T18:00:00",
  "endTime": "2024-10-01T20:00:00",
  "shared": true
}
```

### 列表
**接口**：`GET /api/couple/calendar`

### 删除
**接口**：`DELETE /api/couple/calendar/{id}`

## 4) 共同待办

### 创建待办
**接口**：`POST /api/couple/todos`  
**Body (JSON)**：
```json
{
  "content": "预定酒店",
  "dueTime": "2024-09-01T12:00:00"
}
```

### 列表
**接口**：`GET /api/couple/todos`

### 切换完成
**接口**：`POST /api/couple/todos/{id}/toggle`

### 删除
**接口**：`DELETE /api/couple/todos/{id}`

## 5) 留言板

### 添加留言
**接口**：`POST /api/couple/messages`  
**Body (JSON)**：
```json
{
  "content": "今天很开心～"
}
```

### 列表
**接口**：`GET /api/couple/messages`

### 删除
**接口**：`DELETE /api/couple/messages/{id}`

## 6) 里程碑

### 创建里程碑
**接口**：`POST /api/couple/milestones`  
**Body (JSON)**：
```json
{
  "title": "第一次旅行",
  "description": "去杭州",
  "eventDate": "2024-05-01"
}
```

### 列表
**接口**：`GET /api/couple/milestones`

### 删除
**接口**：`DELETE /api/couple/milestones/{id}`

## 7) 重要日期

### 创建重要日期
**接口**：`POST /api/couple/important-dates`  
**Body (JSON)**：
```json
{
  "title": "恋爱纪念日",
  "date": "2020-10-01",
  "remindDays": 7
}
```

### 列表
**接口**：`GET /api/couple/important-dates`

### 删除
**接口**：`DELETE /api/couple/important-dates/{id}`
