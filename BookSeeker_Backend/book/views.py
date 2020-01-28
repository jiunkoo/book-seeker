from rest_framework import viewsets
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

from book.models import ComicInfo
from book.models import RomanceInfo
from book.models import FantasyInfo

from book.serializers import ComicInfoSerializer
from book.serializers import RomanceInfoSerializer
from book.serializers import FantasyInfoSerializer


# BookInfoViewSet() : View와 연동해서 DB에 튜플을 삽입하고 꺼내서 보여주는 구문
class BookInfoViewSet(viewsets.ModelViewSet):
    comic_queryset = ComicInfo.objects.all()
    romance_queryset = RomanceInfo.objects.all()
    fantasy_queryset = FantasyInfo.objects.all()
    queryset = comic_queryset.union(romance_queryset, fantasy_queryset).order_by('date')
    serializer_class = ComicInfoSerializer
    # 페이징 클래스 적용
    pagination_class = PageNumberPagination

    def get_queryset(self):
        comic_queryset = ComicInfo.objects.all()
        romance_queryset = RomanceInfo.objects.all()
        fantasy_queryset = FantasyInfo.objects.all()
        queryset = comic_queryset.union(romance_queryset, fantasy_queryset).order_by('date')
        return queryset

    def list(self, request, *args, **kwargs):
        queryset = self.set_filters(self.get_queryset(), request)

        self.paginator.page_size_query_param = "page_size"
        page = self.paginate_queryset(queryset)

        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def set_filters(self, queryset, request):
        searchWord = request.query_params.get('searchWord', None)
        category = request.GET.get('category', '')

        if searchWord is not None:
            comic_queryset = ComicInfo.objects.all().filter(title__contains=searchWord)
            romance_queryset = RomanceInfo.objects.all().filter(title__contains=searchWord)
            fantasy_queryset = FantasyInfo.objects.all().filter(title__contains=searchWord)
            queryset = comic_queryset.union(romance_queryset, fantasy_queryset)
            # queryset = queryset.filter(title__contains=title)

        if category == '0':  # 발행일 최신 순 정렬
            comic_queryset = ComicInfo.objects.all().filter(title__contains=searchWord)
            romance_queryset = RomanceInfo.objects.all().filter(title__contains=searchWord)
            fantasy_queryset = FantasyInfo.objects.all().filter(title__contains=searchWord)
            queryset = comic_queryset.union(romance_queryset, fantasy_queryset).order_by('-date')
        elif category == '1':  # 발행일 오래된 순 정렬
            comic_queryset = ComicInfo.objects.all().filter(title__contains=searchWord)
            romance_queryset = RomanceInfo.objects.all().filter(title__contains=searchWord)
            fantasy_queryset = FantasyInfo.objects.all().filter(title__contains=searchWord)
            queryset = comic_queryset.union(romance_queryset, fantasy_queryset).order_by('date')
        elif category == '2':  # 내가 평가한 순 정렬
            comic_queryset = ComicInfo.objects.all().filter(title__contains=searchWord)
            romance_queryset = RomanceInfo.objects.all().filter(title__contains=searchWord)
            fantasy_queryset = FantasyInfo.objects.all().filter(title__contains=searchWord)
            queryset = comic_queryset.union(romance_queryset, fantasy_queryset).order_by('title')
        return queryset

# ComicInfoViewSet() : View와 연동해서 DB에 튜플을 삽입하고 꺼내서 보여주는 구문
class ComicInfoViewSet(viewsets.ModelViewSet):
    queryset = ComicInfo.objects.all()
    serializer_class = ComicInfoSerializer
    # 페이징 클래스 적용
    pagination_class = PageNumberPagination

    def get_queryset(self):
        queryset = ComicInfo.objects.all()
        return queryset

    def list(self, request, *args, **kwargs):
        queryset = self.set_filters(self.get_queryset(), request)

        self.paginator.page_size_query_param = "page_size"
        page = self.paginate_queryset(queryset)

        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def set_filters(self, queryset, request):
        title = request.query_params.get('title', None)
        category = request.GET.get('category', '')

        if title is not None:
            queryset = queryset.filter(title__contains=title)

        if category == '0':  # 랜덤 정렬
            queryset = queryset.order_by('?')
        elif category == '1':  # 발행일 최신 순 정렬
            queryset = queryset.order_by('-date')
        elif category == '2':  # 발행일 오래된 순 정렬
            queryset = queryset.order_by('date')
        elif category == '3':  # 내가 평가한 순 정렬
            queryset = queryset.order_by('title')
        elif category == '4':  # 내가 평가하지 않은 순 정렬
            queryset = queryset.order_by('-title')

        return queryset

# RomanceInfoViewSet() : View와 연동해서 DB에 튜플을 삽입하고 꺼내서 보여주는 구문
class RomanceInfoViewSet(viewsets.ModelViewSet):
    queryset = RomanceInfo.objects.all()
    serializer_class = RomanceInfoSerializer
    # 페이징 클래스 적용
    pagination_class = PageNumberPagination

    def get_queryset(self):
        queryset = RomanceInfo.objects.all()
        return queryset

    def list(self, request, *args, **kwargs):
        queryset = self.set_filters(self.get_queryset(), request)

        self.paginator.page_size_query_param = "page_size"
        page = self.paginate_queryset(queryset)

        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def set_filters(self, queryset, request):
        title = request.query_params.get('title', None)
        category = request.GET.get('category', '')

        if title is not None:
            queryset = queryset.filter(title__contains=title)

        if category == '0':  # 랜덤 정렬
            queryset = queryset.order_by('?')
        elif category == '1':  # 발행일 최신 순 정렬
            queryset = queryset.order_by('-date')
        elif category == '2':  # 발행일 오래된 순 정렬
            queryset = queryset.order_by('date')
        elif category == '3':  # 내가 평가한 순 정렬
            queryset = queryset.order_by('title')
        elif category == '4':  # 내가 평가하지 않은 순 정렬
            queryset = queryset.order_by('-title')

        return queryset


# FantasyInfoViewSet() : View와 연동해서 DB에 튜플을 삽입하고 꺼내서 보여주는 구문
class FantasyInfoViewSet(viewsets.ModelViewSet):
    queryset = FantasyInfo.objects.all()
    serializer_class = FantasyInfoSerializer
    # 페이징 클래스 적용
    pagination_class = PageNumberPagination

    def get_queryset(self):
        queryset = FantasyInfo.objects.all()
        return queryset

    def list(self, request, *args, **kwargs):
        queryset = self.set_filters(self.get_queryset(), request)

        self.paginator.page_size_query_param = "page_size"
        page = self.paginate_queryset(queryset)

        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def set_filters(self, queryset, request):
        title = request.query_params.get('title', None)
        category = request.GET.get('category', '')

        if title is not None:
            queryset = queryset.filter(title__contains=title)

        if category == '0':  # 랜덤 정렬
            queryset = queryset.order_by('?')
        elif category == '1':  # 발행일 최신 순 정렬
            queryset = queryset.order_by('-date')
        elif category == '2':  # 발행일 오래된 순 정렬
            queryset = queryset.order_by('date')
        elif category == '3':  # 내가 평가한 순 정렬
            queryset = queryset.order_by('title')
        elif category == '4':  # 내가 평가하지 않은 순 정렬
            queryset = queryset.order_by('-title')

        return queryset
