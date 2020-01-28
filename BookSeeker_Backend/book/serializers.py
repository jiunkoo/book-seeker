from rest_framework import serializers
from book.models import ComicInfo
from book.models import RomanceInfo
from book.models import FantasyInfo

# ComicInfoSerializer : queryset 등의 복잡한 데이터를 JSON 등의 콘텐츠로 변환하는 클래스
class ComicInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model = ComicInfo
        fields = ('id', 'bsin', 'title', 'author', 'publisher', 'introduction', 'image', 'link', 'keyword', 'adult', 'seperator', 'date')

# RomanceInfoSerializer : queryset 등의 복잡한 데이터를 JSON 등의 콘텐츠로 변환하는 클래스
class RomanceInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model = RomanceInfo
        fields = ('id', 'bsin', 'title', 'author', 'publisher', 'introduction', 'image', 'link', 'keyword', 'adult', 'seperator', 'date')

# FantasyInfoSerializer : queryset 등의 복잡한 데이터를 JSON 등의 콘텐츠로 변환하는 클래스
class FantasyInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model = FantasyInfo
        fields = ('id', 'bsin', 'title', 'author', 'publisher', 'introduction', 'image', 'link', 'keyword', 'adult', 'seperator', 'date')