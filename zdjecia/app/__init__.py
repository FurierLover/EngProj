from flask import Flask
from flask_socketio import SocketIO



app = Flask(__name__)
app.config.from_object("config.Config")
socketio = SocketIO(app)

from app import views

