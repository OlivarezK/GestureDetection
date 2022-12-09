from flask import Flask
from flask import request
import os

app = Flask(__name__)

@app.route('/')
def test_api():
    return 'API is working normally'

@app.route('/post', methods=["POST"])
def post_gesture():
    value = request.form['value']
    file_num = request.form['fileNum']
    
    write_file(value, file_num)
    
    return("Gesture recorded successfully!")

def write_file(data, file_number):
    save_path = '../Data'
    file_name = f"gesture_{file_number}"
    file_path = os.path.join(save_path, file_name)
    with open(file_path, "w+") as f:
        f.write(data)
