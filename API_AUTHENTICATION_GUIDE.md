# API 认证使用指南

## 概述

本系统使用 JWT (JSON Web Token) 进行接口鉴权。用户需要先通过手机号和验证码登录获取 Token，然后在后续的 API 请求中携带该 Token。

**重要提示**:
- 生产环境中，**所有 API 请求必须通过网关访问**（默认端口：8080）
- 网关会统一进行 JWT Token 验证
- 直接访问各微服务端口将绕过网关鉴权，仅用于开发调试

## 服务端口

| 服务 | 端口 | 用途 |
|------|------|------|
| quant-gateway | 8080 | **统一入口（生产环境使用）** |
| quant-user | 8081 | 用户服务（开发调试） |
| quant-market | 8082 | 行情服务（开发调试） |

## 认证流程

### 1. 发送验证码

**接口**: `POST /api/auth/send-code`

**请求示例**:
```json
{
  "phone": "13800138000"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "isRegistered": false,
    "code": "123456",
    "message": "Verification code sent successfully"
  },
  "timestamp": 1699999999999
}
```

**响应说明**:
- `isRegistered`: 布尔值，表示该手机号是否已注册
  - `false` (code: 2001): 用户未注册，登录时会自动创建账号
  - `true` (code: 200): 用户已注册
- `code`: 验证码（6位数字），V1版本直接返回，生产环境应通过短信发送
- `message`: 提示信息

### 2. 登录获取 Token

**接口**: `POST /api/auth/login`

**请求示例**:
```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "userId": 1,
    "phone": "13800138000",
    "nickName": "User_8000",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInBob25lIjoiMTM4MDAxMzgwMDAiLCJpYXQiOjE2OTk5OTk5OTksImV4cCI6MTcwMDA4NjM5OX0.xxx",
    "isNewUser": true
  },
  "timestamp": 1699999999999
}
```


**响应说明**:
- `userId`: 用户ID
- `phone`: 手机号
- `nickName`: 用户昵称（新注册用户会自动生成）
- `token`: JWT Token，用于后续API调用
- `isNewUser`: 是否为新注册用户

### 3. 使用 Token 访问受保护的接口

**前端实现方式**:

#### 方式一：在 HTTP Header 中携带 Token

在每个需要认证的 API 请求中，添加 `Authorization` 请求头：

```
Authorization: Bearer {token}
```

**示例（使用 axios）**:

```javascript
import axios from 'axios';

// 配置 axios 实例 - 通过网关访问
const api = axios.create({
  baseURL: 'http://localhost:8080'  // 网关端口
});

// 请求拦截器：自动添加 Token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token'); // 从本地存储获取 token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理认证失败
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // Token 无效或过期，跳转到登录页
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

**使用示例**:

```javascript
// 1. 发送验证码
const sendCode = async (phone) => {
  try {
    const response = await api.post('/api/auth/send-code', { phone });
    console.log('验证码:', response.data.data.code);
    console.log('是否已注册:', response.data.data.isRegistered);
    return response.data;
  } catch (error) {
    console.error('发送验证码失败:', error);
  }
};

// 2. 登录
const login = async (phone, code) => {
  try {
    const response = await api.post('/api/auth/login', { phone, code });
    const { token, userId, nickName } = response.data.data;

    // 保存 token 到本地存储
    localStorage.setItem('token', token);
    localStorage.setItem('userId', userId);
    localStorage.setItem('nickName', nickName);

    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
  }
};

// 3. 调用需要认证的接口
const getUserInfo = async (userId) => {
  try {
    // token 会通过拦截器自动添加
    const response = await api.get(`/api/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};
```

#### 方式二：使用 fetch API

```javascript
// 登录
async function login(phone, code) {
  const response = await fetch('http://localhost:8080/api/auth/login', {  // 通过网关
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ phone, code })
  });

  const result = await response.json();
  if (result.code === 200) {
    // 保存 token
    localStorage.setItem('token', result.data.token);
  }
  return result;
}

// 调用需要认证的接口
async function getUserInfo(userId) {
  const token = localStorage.getItem('token');

  const response = await fetch(`http://localhost:8080/api/user/${userId}`, {  // 通过网关
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  return await response.json();
}
```

## Token 说明

### Token 组成

JWT Token 由三部分组成，用点号（.）分隔：
```
Header.Payload.Signature
```

### Token 内容

Payload 中包含以下信息：
- `userId`: 用户ID
- `phone`: 手机号
- `iat`: 签发时间
- `exp`: 过期时间

### Token 有效期

默认有效期为 **24 小时**（86400000 毫秒），可通过环境变量 `JWT_EXPIRATION` 配置。

### Token 存储建议

1. **Web 应用**: 使用 `localStorage` 或 `sessionStorage`
2. **移动应用**: 使用安全存储（如 iOS Keychain、Android Keystore）
3. **不要**将 Token 存储在 Cookie 中（如果不做 CSRF 防护）

## 错误码说明

| Code | 说明 |
|------|------|
| 200  | 成功 |
| 401  | 未授权（未提供 Token 或 Token 无效） |
| 2001 | 用户未注册 |
| 2002 | 用户已注册 |
| 2003 | 手机号格式错误 |
| 2004 | 验证码无效或已过期 |
| 2006 | Token 无效 |
| 2007 | Token 已过期 |

## 安全建议

1. **使用 HTTPS**: 生产环境必须使用 HTTPS 传输，防止 Token 被窃取
2. **Token 刷新**: 建议实现 Token 刷新机制，避免用户频繁重新登录
3. **退出登录**: 退出时清除本地存储的 Token
4. **Token 泄露**: 如果怀疑 Token 泄露，应立即更改密码或重新登录获取新 Token

## 完整示例（React）

```javascript
// authService.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';  // 网关地址

const api = axios.create({
  baseURL: API_BASE_URL
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // 清除 token 并跳转到登录页
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  // 发送验证码
  sendCode: async (phone) => {
    const response = await api.post('api/auth/send-code', { phone });
    return response.data;
  },

  // 登录
  login: async (phone, code) => {
    const response = await api.post('/api/auth/login', { phone, code });
    if (response.data.code === 200) {
      const { token, userId, nickName } = response.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId);
      localStorage.setItem('nickName', nickName);
    }
    return response.data;
  },

  // 退出登录
  logout: () => {
    localStorage.clear();
    window.location.href = '/login';
  },

  // 获取当前用户信息
  getCurrentUser: () => {
    return {
      userId: localStorage.getItem('userId'),
      nickName: localStorage.getItem('nickName'),
      token: localStorage.getItem('token')
    };
  },

  // 检查是否已登录
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  }
};

export default api;
```

```javascript
// LoginPage.jsx
import React, { useState } from 'react';
import { authService } from './authService';

function LoginPage() {
  const [phone, setPhone] = useState('');
  const [code, setCode] = useState('');
  const [countdown, setCountdown] = useState(0);

  const handleSendCode = async () => {
    try {
      const result = await authService.sendCode(phone);
      if (result.code === 200) {
        alert(`验证码: ${result.data.code}`);
        // 开始倒计时
        setCountdown(60);
        const timer = setInterval(() => {
          setCountdown(prev => {
            if (prev <= 1) {
              clearInterval(timer);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);
      }
    } catch (error) {
      alert('发送验证码失败');
    }
  };

  const handleLogin = async () => {
    try {
      const result = await authService.login(phone, code);
      if (result.code === 200) {
        alert('登录成功');
        window.location.href = '/dashboard';
      }
    } catch (error) {
      alert('登录失败');
    }
  };

  return (
    <div>
      <input
        type="tel"
        value={phone}
        onChange={e => setPhone(e.target.value)}
        placeholder="请输入手机号"
      />
      <button
        onClick={handleSendCode}
        disabled={countdown > 0}
      >
        {countdown > 0 ? `${countdown}秒后重试` : '发送验证码'}
      </button>
      <input
        type="text"
        value={code}
        onChange={e => setCode(e.target.value)}
        placeholder="请输入验证码"
      />
      <button onClick={handleLogin}>登录</button>
    </div>
  );
}

export default LoginPage;
```

## 环境变量配置

在生产环境中，建议通过环境变量配置以下参数：

```bash
# JWT 密钥（至少 256 位）
JWT_SECRET=your-very-secure-secret-key-at-least-256-bits-long

# JWT 过期时间（毫秒）
JWT_EXPIRATION=86400000

# 数据库配置
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=quant_user
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your-secure-password
```

## 网关路由配置

### 路由规则

通过网关访问时，路由规则如下：

| 原始路径               | 网关路径               | 目标服务 | 是否需要鉴权 |
|--------------------|--------------------|---------|-------------|
| `/api/auth/**`     | `/api/auth/**`     | quant-user | ❌ 否 |
| `/api//user/**`    | `/api//user/**`         | quant-user | ✅ 是 |
| `/api/stocks/**`   | `/api/stocks/**`   | quant-market | ✅ 是 |
| `/api/trade/**`    | `/api/trade/**`    | quant-trade | ✅ 是 |
| `/api/risk/**`     | `/api/risk/**`     | quant-risk | ✅ 是 |
| `/api/strategy/**` | `/api/strategy/**` | quant-strategy | ✅ 是 |

### 白名单路径（无需 Token）

以下路径无需提供 Token，可直接访问：
- `/api/auth/**` - 认证相关接口（登录、发送验证码）
- `/actuator/**` - 监控接口
- `/error` - 错误页面
- `/favicon.ico` - 网站图标

### 访问示例

```javascript
// ✅ 正确 - 通过网关访问
const API_BASE_URL = 'http://localhost:8080';

// 登录（无需 Token）
await axios.post(`${API_BASE_URL}/api/auth/login`, { phone, code });

// 获取用户信息（需要 Token）
await axios.get(`${API_BASE_URL}/api/user/123`, {
  headers: { 'Authorization': `Bearer ${token}` }
});

// 获取股票信息（需要 Token）
await axios.get(`${API_BASE_URL}/api/stocks/1`, {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

```javascript
// ❌ 错误 - 直接访问微服务（绕过网关鉴权）
// 仅用于开发调试，生产环境禁止
const API_BASE_URL = 'http://localhost:8081';  // 直接访问用户服务
```

## 网关鉴权机制

### 工作原理

1. **请求到达网关**：所有请求首先到达网关（8080端口）
2. **白名单检查**：网关检查请求路径是否在白名单中
3. **Token 验证**：非白名单路径需要验证 `Authorization` Header
4. **JWT 解析**：验证 Token 签名和有效期
5. **用户信息注入**：将用户信息添加到请求头 `X-User-Id` 和 `X-User-Phone`
6. **路由转发**：转发请求到对应的微服务

### 网关返回的错误

网关鉴权失败时，会返回：

```json
{
  "code": 401,
  "message": "Unauthorized - Invalid or missing token",
  "timestamp": 1699999999999
}
```

### 下游服务获取用户信息

微服务可以从请求头中获取当前用户信息：

```java
// 在 Spring Boot Controller 中
@GetMapping("/profile")
public Result<UserProfile> getProfile(
    @RequestHeader("X-User-Id") Long userId,
    @RequestHeader("X-User-Phone") String phone
) {
    // 使用 userId 和 phone
    return Result.success(userService.getProfile(userId));
}
```

## 常见问题

### Q1: 为什么要使用网关？
**A**: 网关提供统一的入口，集中处理鉴权、路由、限流等横切关注点，避免每个服务重复实现。

### Q2: 开发环境可以直接访问微服务吗？
**A**: 可以，但仅用于调试。生产环境必须通过网关访问。

### Q3: 如何在本地启动整个系统？
**A**:
```bash
# 1. 启动基础设施（Nacos、PostgreSQL等）
./start-infra.sh

# 2. 启动网关
cd quant-gateway && mvn spring-boot:run

# 3. 启动用户服务
cd quant-user && mvn spring-boot:run

# 4. 启动其他服务（可选）
cd quant-market && mvn spring-boot:run
```

### Q4: Token 过期后如何处理？
**A**: 前端需要捕获 401 错误，清除本地 Token，引导用户重新登录。

### Q5: 可以同时在多个设备登录吗？
**A**: 可以。每次登录会生成新的 Token，多个 Token 可以同时有效（直到过期）。
