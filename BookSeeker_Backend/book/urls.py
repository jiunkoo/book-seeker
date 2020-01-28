from django.urls import include, path


app_name = 'book'

urlpatterns = [
    path('', include('rest_framework.urls', namespace='rest_framework_category')),
]