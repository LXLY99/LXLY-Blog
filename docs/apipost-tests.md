# Apipost 测试脚本示例（新增功能）

> 说明：以下为 Apipost 中「Tests」脚本示例与请求配置建议，可直接复制到对应接口的测试脚本区域。

## 1) 公开文章检索

**接口**：`GET /api/home/articles`  
**参数**：`page`、`size`、`q`（可选）

**测试脚本（Tests）**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("response has data field", function () {
  var json = pm.response.json();
  pm.expect(json).to.have.property("data");
});
```

**示例请求**：
```
GET {{baseUrl}}/api/home/articles?page=1&size=10&q=关键词
```

## 2) 发表评论（需登录）

**接口**：`POST /api/comments`  
**Header**：`Authorization: Bearer {{token}}`  
**Body (JSON)**：
```json
{
  "articleId": 1,
  "content": "这是一条测试评论",
  "parentId": 0
}
```

**测试脚本（Tests）**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("response data has id", function () {
  var json = pm.response.json();
  pm.expect(json).to.have.property("data");
  pm.expect(json.data).to.have.property("id");
});
```

## 3) 获取评论列表（公开）

**接口**：`GET /api/comments?articleId=1`

**测试脚本（Tests）**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("response data is array", function () {
  var json = pm.response.json();
  pm.expect(json).to.have.property("data");
  pm.expect(json.data).to.be.an("array");
});
```

**示例请求**：
```
GET {{baseUrl}}/api/comments?articleId=1
```

## 4) 图片上传（类型/大小校验）

**接口**：`POST /api/upload/image`  
**Header**：`Authorization: Bearer {{token}}`  
**Body**：`form-data`  
**字段**：`file`（选择图片文件，建议 jpeg/png/webp/gif）

**测试脚本（Tests）**：
```javascript
pm.test("status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("response has url", function () {
  var json = pm.response.json();
  pm.expect(json).to.have.property("data");
  pm.expect(json.data).to.have.property("url");
});
```

## 5) 图片上传失败（非法类型/空文件）

**接口**：`POST /api/upload/image`  
**Header**：`Authorization: Bearer {{token}}`  
**Body**：`form-data`  
**字段**：`file`（选择非图片文件）

**测试脚本（Tests）**：
```javascript
pm.test("status code is 400", function () {
  pm.response.to.have.status(400);
});

pm.test("response has message", function () {
  var json = pm.response.json();
  pm.expect(json).to.have.property("message");
});
```
