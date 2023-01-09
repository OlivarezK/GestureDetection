from flask import Flask
from flask import request
from datetime import datetime
import os
import json

app = Flask(__name__)


@app.route('/')
def test_api():
    return 'API is working normally'


@app.route('/post', methods=["POST"])
def post_gesture():
    benchmark_results = request.json

    save_report(benchmark_results)

    return ("Benchmark recorded successfully!")


def save_report(benchmark_results):
    save_path = '/reports'
    timestamp = datetime.now()
    file_name = f"benchmark_{timestamp}"
    file_path = os.path.join(save_path, file_name)
    file_content = json.dumps(benchmark_results)
    with open(file_path, "w+") as f:
        f.write(file_content, indent=2)
