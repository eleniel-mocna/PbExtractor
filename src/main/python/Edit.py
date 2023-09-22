import re
from enum import Enum
from typing import List


class Edit(Enum):
    INSERT = "insert"
    DELETE = "delete"
    REPLACE = "replace"

    @staticmethod
    def decipher_str(s: str) -> List["Edit"]:
        edit_strings = re.findall(r"(D\(\[.*?] @ \d+\)|I\(\[.*?] @ \d+\)|R\(\[.*?] -> \[.*?] @ \d+\))", s)
        return [Edit.from_str(s) for s in edit_strings]

    @staticmethod
    def from_str(part):
        if part.startswith("D"):
            return Edit.DELETE
        elif part.startswith("I"):
            return Edit.INSERT
        elif part.startswith("R"):
            return Edit.REPLACE
        else:
            raise ValueError(f"Unknown edit type: {part}")
