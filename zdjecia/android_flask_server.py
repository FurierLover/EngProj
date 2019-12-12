import flask
import werkzeug

app = flask.Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)
    return "Image Uploaded Successfully"


if __name__ == '__main__':
    app.run(host='192.168.0.178', port=5000, debug=True)
