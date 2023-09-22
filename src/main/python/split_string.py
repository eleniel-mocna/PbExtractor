from typing import List

import regex


def split_string(s: str) -> List[str]:
    result = regex.split(r"([_.,;~ˇ^˘°˛`˙˝¨\"\'\s])|(?=[A-Z])|(?<=[\p{Punct}])", s)
    return [x for x in result if x and not x.isspace()]
