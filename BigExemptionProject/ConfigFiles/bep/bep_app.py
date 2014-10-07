#!flask/bin/python
import os
from flask import Flask, request, redirect, url_for, render_template
from werkzeug import secure_filename
import subprocess
import yaml

UPLOAD_FOLDER = '/home/ubuntu/bep/uploads'
ALLOWED_EXTENSIONS = set(['xsd', 'xtl'])

app = Flask(__name__, static_url_path='')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 20 * 1024 * 1024

@app.route('/')
def index():
    return app.send_static_file('index.html')

@app.route('/', methods=['POST'])
def company_name():
    text = request.form['company']

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

            if (filename.endswith('xsd')):
                jsontree = yaml.load(subprocess.check_output(['./parse_schema.py', 
                            os.path.join(app.config['UPLOAD_FOLDER'], filename)]))
            elif (filename.endswith('xtl')):
                jsontree = yaml.load(subprocess.check_output(['./parse_xtl.py', 
                            os.path.join(app.config['UPLOAD_FOLDER'], filename)]))
            
            treeroot = {'title' : jsontree[0].get('title')}
            treebody = jsontree[0].get('children')
            return render_template('treelist.html', treeroot=treeroot, treebody=treebody)



if __name__ == '__main__':
    app.run(port=8000, debug=False)

