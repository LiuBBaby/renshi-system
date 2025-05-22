// 应用全局配置

// API地址配置
let baseUrl = 'http://localhost:8080'; // 默认使用localhost

// #ifdef MP-WEIXIN
try {
  // 获取系统信息，区分环境
  const systemInfo = wx.getSystemInfoSync();

  // 只根据平台类型判断
  if (systemInfo.platform !== 'devtools') {
    // 真机环境使用IP地址
    baseUrl = 'http://192.168.0.111:8080';
    console.log('真机环境，使用IP地址:', baseUrl);
  } else {
    // 开发者工具环境使用localhost
    console.log('开发者工具环境，使用localhost:', baseUrl);
  }
} catch (e) {
  console.error('获取环境信息失败:', e);
  // 保留默认值或设置一个备用地址
}
// #endif

console.log('当前使用的API地址:', baseUrl);

// 模块对象
const config = {
  // 后端接口地址
  baseUrl: baseUrl,

  // 应用信息
  appInfo: {
    // 应用名称
    name: "ruoyi-app",
    // 应用版本
    version: "1.1.0",
    // 应用logo
    logo: "/static/logo.png",
    // 官方网站
    site_url: "http://ruoyi.vip",
    // 政策协议
    agreements: [{
        title: "隐私政策",
        url: "https://ruoyi.vip/protocol.html"
      },
      {
        title: "用户服务协议",
        url: "https://ruoyi.vip/protocol.html"
      }
    ]
  }
};

module.exports = config;