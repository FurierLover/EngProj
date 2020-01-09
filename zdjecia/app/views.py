import uuid
from app import app, socketio
from flask import render_template, request, redirect, url_for, session
from werkzeug.utils import secure_filename
from .mesh_thread import MeshThread

import os

# app.config["IMAGE_UPLOADS"] = "/Users/ola/zdjecia/app/static"
app.config["ALLOWED_IMAGE_EXTENSIONS"] = ["JPEG", "JPG", "PNG", "GIF"]
app.config["MAX_IMAGE_FILESIZE"] = 0.5 * 1024 * 1024


def allowed_image(filename):
    if not "." in filename:
        return False

    ext = filename.rsplit(".", 1)[1]

    if ext.upper() in app.config["ALLOWED_IMAGE_EXTENSIONS"]:
        return True
    else:
        return False


def allowed_image_filesize(filesize):
    if int(filesize) <= app.config["MAX_IMAGE_FILESIZE"]:
        return True
    else:
        return False


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/upload_image", methods=["GET", "POST"])
def upload_image():
    if request.method == "POST":
        if request.files:

            images = request.files.getlist("image")
            if len(images) == 0:
                print("No files")
                return redirect(request.url)
            else:
                directory_path = os.path.join(app.config.get('IMAGE_UPLOADS'), str(uuid.uuid4()))
                os.mkdir(directory_path)
                session['input_dir'] = directory_path
                for image in images:
                    image.save(os.path.join(directory_path, image.filename))
                return redirect('/uploaded')
    return render_template("upload_image.html")


@app.route('/upload', methods=['POST'])
def upload_from_smartphone():
    if request.files:
        print(len(request.files))
        return '', 200
    else:
        return '', 404


@app.route("/uploaded")
def image_uploaded():
    input_directory = session.get('input_dir', None)
    output_directory = os.path.join(input_directory, 'output')
    output_filename = os.path.join(output_directory, 'texturedMesh.obj')
    os.mkdir(output_directory)
    if input_directory is None:
        return redirect('/upload_image')

    socketio.emit('my event', {"message": "Working..."})

    mesh_thread = MeshThread(app.config['PATH_TO_MESHROOM'], input_directory, output_directory, socketio)
    mesh_thread.start()
    mesh_thread.join()

    return render_template("image_uploaded.html", object_url=output_filename)
