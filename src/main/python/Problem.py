from typing import Dict, Any, List

import numpy as np
from Levenshtein import distance

from src.main.python.Edit import Edit
from src.main.python.Example import Example
from src.main.python.constants import EXAMPLES, EDITS, INPUT, TEXT, OUTPUT, SYNTHESIZABLE_FROM_FIRST
from src.main.python.split_string import split_string


class Problem:
    def __init__(self, first: Example, second: Example, synthesizable: bool):
        self.first = first
        self.second = second
        self.synthesizable = synthesizable

    @staticmethod
    def feature_names():
        return [
            "first_input_len",
            "first_output_len",
            "second_input_len",
            "second_output_len",
            "first_edits_len",
            "second_edits_len",
            "first_delete_ratio",
            "first_insert_ratio",
            "first_replace_ratio",
            "second_delete_ratio",
            "second_insert_ratio",
            "second_replace_ratio",
            "first_input_levenshtein",
            "first_output_levenshtein",
            "edits_levenshtein",
            "edits_levenshtein_ratio"
        ]

    def extract_features(self):
        features = np.zeros(16)

        features[0] += (len(split_string(self.first.input_str)))
        features[1] += (len(split_string(self.first.output_str)))
        features[2] += (len(split_string(self.second.input_str)))
        features[3] += (len(split_string(self.second.output_str)))

        first_edits_len = len(self.first.edits)
        second_edits_len = len(self.second.edits)
        features[4] += first_edits_len
        features[5] += second_edits_len

        features[6] += (len([e for e in self.first.edits if e == Edit.DELETE]) / first_edits_len)
        features[7] += (len([e for e in self.first.edits if e == Edit.INSERT]) / first_edits_len)
        features[8] += (len([e for e in self.first.edits if e == Edit.REPLACE]) / first_edits_len)

        features[9] += (len([e for e in self.second.edits if e == Edit.DELETE]) / second_edits_len)
        features[10] += (len([e for e in self.second.edits if e == Edit.INSERT]) / second_edits_len)
        features[11] += (len([e for e in self.second.edits if e == Edit.REPLACE]) / second_edits_len)

        features[12] += (distance(split_string(self.first.input_str), split_string(self.second.input_str)))
        features[13] += (distance(split_string(self.first.output_str), split_string(self.second.output_str)))
        edits_distance = distance(self.first.edits, self.second.edits)
        features[14] += edits_distance
        features[15] += (edits_distance / (first_edits_len + second_edits_len))

        return features


def create_problem(problem_data: Dict[str, Any], first_example: Example):
    for i in range(1, len(problem_data[EXAMPLES])):
        edits = Edit.decipher_str(problem_data[EXAMPLES][i][EDITS])
        if not edits:
            continue
        second_example = Example(problem_data[EXAMPLES][i][INPUT][TEXT], problem_data[EXAMPLES][i][OUTPUT][TEXT],
                                 edits)
        yield Problem(first_example, second_example, problem_data[SYNTHESIZABLE_FROM_FIRST][i])


def create_problems_from_json(input_json: List[Dict[str, Any]]) -> List[Problem]:
    problems = []
    for problem_data in input_json:
        first_edits = Edit.decipher_str(problem_data[EXAMPLES][0][EDITS])
        if not first_edits:
            continue
        first_example = Example(problem_data[EXAMPLES][0][INPUT][TEXT], problem_data[EXAMPLES][0][OUTPUT][TEXT],
                                first_edits)
        problems.extend(create_problem(problem_data, first_example))
    return problems
