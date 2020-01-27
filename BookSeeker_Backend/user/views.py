from rest_framework import permissions, generics
from rest_framework.response import Response
from django.contrib.auth.models import User

from knox.models import AuthToken
from rest_framework.utils import json

from user.serializers import RegisterSerializer, LoginSerializer, UserSerializer


# Register API
class RegisterAPI(generics.GenericAPIView):
    serializer_class = RegisterSerializer

    def post(self, request, *args, **kwargs):
        body = json.loads(request.body.decode('utf-8'))
        email = body.get("email")
        username = body.get("username")

        emailCheck = User.objects.filter(email=email).exists()
        usernameCheck = User.objects.filter(username=username).exists()

        if emailCheck == True:
            if usernameCheck == True:  # Email과 Username이 중복되는 경우
                return Response(0)
            else:  # Email은 중복되고 Username은 중복되지 않는 경우
                return Response(1)
        else:
            if usernameCheck == True:  # Email은 중복되지 않고 Username은 중복되는 경우
                return Response(2)
            else:  # Email과 Username이 둘 다 중복되지 않는 경우
                serializer = self.get_serializer(data=request.data)
                serializer.is_valid(raise_exception=True)
                serializer.save()
                return Response(3)

# Login API
class LoginAPI(generics.GenericAPIView):
    serializer_class = LoginSerializer

    def post(self, request, *args, **kwargs):
        body = json.loads(request.body.decode('utf-8'))
        email = body.get("username")
        password = body.get("password")

        emailCheck = User.objects.filter(email=email).exists()

        if emailCheck == True:  # 만일 Email이 존재하는 경우
            user = User.objects.get(email=email)
            if user.check_password(password):  # 만일 Email과 Password가 일치하는 경우
                serializer = self.get_serializer(data=request.data)
                serializer.is_valid(raise_exception=True)
                user = serializer.validated_data
                # 위의 validated_data로써 계정 인증
                return Response({
                    "status": "2",
                    "loginUser": UserSerializer(user, context=self.get_serializer_context()).data,
                    "token": AuthToken.objects.create(user)[1]
                })
            else:  # Email은 일치하지만 Password가 일치하지 않는 경우
                return Response({
                    "status": "1"
                })
        else:  # Email과 Password가 둘 다 일치하지 않는 경우
            return Response({
                "status": "0"
            })

# User API
class UserAPI(generics.RetrieveAPIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = UserSerializer

    def get_object(self):
        return self.request.user