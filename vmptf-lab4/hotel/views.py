from django.db import sync_to_async
from django.shortcuts import render
from .models import Booking, Room, Client

# Create your views here.
@sync_to_async
def create_booking(client_id, room_id, check_in, check_out):
    client = Client.objects.get(id=client_id)
    room = Room.objects.get(id=room_id)

    return Booking.objects.create(
        client=client,
        room=room,
        check_in=check_in,
        check_out=check_out
    )

@sync_to_async
def delete_booking(booking_id):
    booking = Booking.objects.get(id=booking_id)
    booking.delete()

@sync_to_async
def get_available_rooms():
    return list(Room.objects.all())