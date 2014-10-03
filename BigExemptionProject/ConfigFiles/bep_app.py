#!flask/bin/python
import os
from flask import Flask, request, redirect, url_for
from werkzeug import secure_filename


UPLOAD_FOLDER = '/home/ubuntu/bep/uploads'
ALLOWED_EXTENSIONS = set(['xsd'])

app = Flask(__name__, static_url_path='')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/')
def index():
    return app.send_static_file('index.html')

@app.route('/', methods=['POST'])
def sender_name():
    text = request.form['sender']

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

@app.route('/upload_file', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        file = request.files['fileupload']
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return app.send_static_file('tree.html')


if __name__ == '__main__':
    app.run(port=8000, debug=True)

