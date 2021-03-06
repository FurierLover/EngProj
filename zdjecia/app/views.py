import uuid
from app import app, socketio
from flask import render_template, request, redirect, url_for, session, send_file
from werkzeug.utils import secure_filename
from .mesh_thread import MeshThread
import os
import zipfile

import os

# app.config["IMAGE_UPLOADS"] = "/Users/ola/zdjecia/app/static"
app.config["ALLOWED_IMAGE_EXTENSIONS"] = ["JPEG", "JPG", "PNG", "GIF"]
app.config["MAX_IMAGE_FILESIZE"] = 0.5 * 1024 * 1024


def allowed_image(filename):
    if "." not in filename:
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
        images = [request.files['upload1'],
                  request.files['upload2'],
                  request.files['upload3'],
                #  request.files['upload4'],
                #  request.files['upload5'],
                  ]
        directory_path = os.path.join(app.config.get('IMAGE_UPLOADS'), str(uuid.uuid4()))
        os.mkdir(directory_path)
        for image in images:
            image.save(os.path.join(directory_path, image.filename))

        output_directory = os.path.join(directory_path, 'output')
        output_filename = os.path.join(output_directory, 'texturedMesh.obj')
        os.mkdir(output_directory)
        if directory_path is None:
            return redirect('/upload_image')

        socketio.emit('my event', {"message": "Working..."})

        mesh_thread = MeshThread(app.config['PATH_TO_MESHROOM'], directory_path, output_directory, socketio)
        mesh_thread.start()
        mesh_thread.join()
        if mesh_thread.success:
            zipfile_name = os.path.join(directory_path, 'output.zip')
            zipf = zipfile.ZipFile(zipfile_name, 'w', zipfile.ZIP_DEFLATED)
            for root, dirs, files in os.walk(output_directory):
                for file in files:
                    zipf.write(os.path.join(root, file))
            zipf.close()
            return zipfile_name
        else:
            return 'Error while processing images', 404

    else:
        return 'Error! no files were sent!', 404


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
    if mesh_thread.success:
        zipfile_name = os.path.join(input_directory, 'output.zip')
        zipf = zipfile.ZipFile(zipfile_name, 'w', zipfile.ZIP_DEFLATED)
        for root, dirs, files in os.walk(output_directory):
            for file in files:
                zipf.write(os.path.join(root, file))
        zipf.close()
        return send_file(zipfile_name, mimetype='application/zip')
    else:
        return 'Error while processing images', 404

    return render_template("image_uploaded.html", object_url=output_filename)
