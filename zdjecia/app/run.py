from app import app, socketio

if __name__ == "__main__":
    #app.run(host="0.0.0.0")
    #app.run()
    socketio.run(app, host='0.0.0.0')