from app import socketio


@socketio.on('my event')
def connect(json, methods=['GET', 'POST']):
    print("connected")