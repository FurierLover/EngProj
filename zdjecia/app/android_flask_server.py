# from flask import Flask, request, render_template
# import werkzeug
#
# app = Flask(__name__)
#
#
# @app.route('/', methods=['GET', 'POST'])
# def handle_request():
#
#     if request.method == 'POST':
#         imagefile = request.files['image']
#         filename = werkzeug.utils.secure_filename(imagefile.filename)
#         print("\nReceived image File name : " + imagefile.filename)
#         imagefile.save(filename)
#     return render_template('')
#
#
# if __name__ == '__main__':
#     app.run(host='192.168.0.178', port=5000, debug=True)
