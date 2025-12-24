import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStorageService } from '../../services/storage-service/StorageService';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const PUBLIC_ENDPOINTS = [
    '/auth/login',
    '/auth/register',
    '/auth/refresh-token',
    '/auth/verify-account',
    '/auth/forgot-password',
    '/auth/reset-password'
  ];

  const url = req.url;
  let path = url;

  // لو كان full URL (يبدأ بـ http:// أو https://)
  if (url.startsWith('http://') || url.startsWith('https://')) {
    try {
      const urlObj = new URL(url);
      path = urlObj.pathname;
    } catch {
      // لو URL مش صحيح، استخدم الـ url كما هو
      path = url;
    }
  }

  console.log('🔄 Interceptor - Request URL:', url);
  console.log('🔄 Interceptor - Request Path:', path);

  // تحقق إذا كان الـ endpoint يبدأ بأي من الـ public endpoints
  const isPublic = PUBLIC_ENDPOINTS.some(endpoint => {
    // path يجب يبدأ بـ endpoint
    const isPublicEndpoint = path.startsWith(endpoint);
    console.log(`Checking if ${path} starts with ${endpoint}: ${isPublicEndpoint}`);
    return isPublicEndpoint;
  });

  console.log('🔍 Is public endpoint?', isPublic);

  if (isPublic) {
    console.log('✅ Interceptor - PUBLIC endpoint, skipping token');
    return next(req);
  }

  console.log('🔒 Interceptor - PRIVATE endpoint, adding token...');

  // جلب الـ token
  const token = AuthStorageService.getAccessToken();

  if (!token) {
    console.error('❌ Interceptor - No token found for private endpoint!');
    // يمكنك redirect للlogin هنا
    return next(req);
  }

  console.log('✅ Interceptor - Token found, adding to request');

  // إضافة الـ token للـ request
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
