from typing import List

from src.main.python.Edit import Edit


class Example:
    def __init__(self, input_str: str, output_str: str, edits: List[Edit]):
        self.input_str = input_str
        self.output_str = output_str
        self.edits = edits
