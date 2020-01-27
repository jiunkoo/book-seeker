from django.contrib import admin
from django.urls import path, include, re_path
from rest_framework.routers import DefaultRouter

# 라우터 생성 및 view set 등록
router = DefaultRouter()

urlpatterns = [
    re_path('admin/', admin.site.urls),
    path('', include(router.urls)),
    path('user/', include('user.urls')),
]
