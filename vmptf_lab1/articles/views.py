from django.shortcuts import render
from django.http import HttpResponse
from .models import Article
from django.core.paginator import Paginator
from django.db.models import Q

# Create your views here.

def home(request):
    return HttpResponse("Hello Articles")

def articles_by_author(request):
    name = request.GET.get("name")

    if not name:
        return HttpResponse("Please provide author name: ?name=...")

    articles = Article.objects.filter(author=name)

    result = "\n".join([a.title for a in articles])

    return HttpResponse(result if result else "No articles found for this author")

def search_articles(request):
    query = request.GET.get("q")

    articles = Article.objects.all()

    if query:
        articles = articles.filter(
            Q(title__icontains=query) |
            Q(text__icontains=query)
        )
    
    paginator = Paginator(articles, 1)
    page = paginator.get_page(request.GET.get("page"))

    return render(request, "articles/list.html", {
        "page": page,
        "query": query
    })