from django.urls import path
from . import views

urlpatterns = [
    path('', views.home),
    path('by-author/', views.articles_by_author),
    path('list/', views.search_articles),
]