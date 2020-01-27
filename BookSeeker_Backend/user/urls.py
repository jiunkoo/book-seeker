from django.urls import include, path
from user.views import RegisterAPI, LoginAPI, UserAPI

urlpatterns = [
    path('', include('rest_framework.urls', namespace='rest_framework_category')),
    path('', include('knox.urls')),
    path('register', RegisterAPI.as_view()),
    path('login', LoginAPI.as_view()),
    path('', UserAPI.as_view())
]