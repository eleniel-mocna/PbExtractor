# PbExtractor

This repository contains a simple tool for extracting
possible [Programming by Example](https://en.wikipedia.org/wiki/Programming_by_example) problems from a given git
repository using a simple program synthesis algorithm for data filtering.

It has been created as a of [NPFL101](https://ufal.mff.cuni.cz/courses/npfl101) course
at [MFF UK](https://www.mff.cuni.cz/en).

## Usage

To run this tool, Java 11 and Gradle 7.4.2 is needed. All other will be downloaded by gradle.

To run the tool, use the following command:

```bash
./gradlew run -PexecArgs="<path_to_source_repository>,<output_json_file>,<commit_limit>"
```

For example, to run the tool on the intellij-community repository in the same folder as this one, use the following
command:

```bash
./gradlew run -PexecArgs="../intellij-community,intellij-community.json,200000"
```

The output file will be created in the root of this repository.

## Output

The output file is a json file containing a list of possible problems. Each problem is represented by a json object
containing the following fields:

- `"examples"`: list of Example for the problem
- `"synthesizableFromFirst"`: list of booleans, where the i-th element is true if the i-th example is synthesizable from
  the first example
- `"distanceFromFirst"`: list of floats, where the i-th element is the distance between the i-th example and the first
  example

Each Example has the following fields:

- `"input"`: json object with `"text"` field containing the input text
- `"output"`: json object with `"text"` field containing the output text
- `"edits"`: string representation of edits needed to transform the input to the output. Comma separated:
    - `I([text] @ index)` - insert text at index
    - `D([text] @ index)` - delete text at index
    - `R([before] -> [after] @ index)` - replace before with after at index
- `"explanation"`: Last generated explanation for the example (the one used to generate from the output from the first
  one - or self-generating if not applicable)
    - `"explanations"`: list of individual explanations
        - `"condition"` - the condition for this explanation to be used
            - `"type"`: [OnIndex|PreviousToken|NextToken]
            - `"index"/"token"`: the index/token for the condition
        - `"edit"`
            - `"type"`: [Insert|Delete|Replace]
            - `"index"`: the index of the edit
            - `"text"` for deletion and insertion, `"oldText"` and `"newText"` for replacement

A python script for reading the output is provided in the src/main/python folder.

## Algorithm

For data filtering a quite simple algorithm is used. We run through all the diffs (change in a file from a previous
commit to a new one) in a repository and for each diff we look at all 1 line edits (or first line from the new text and
last line from the old text, if diff is longer than one line)
and check these, whether they fulfill the following requirements:

- Regularized levenshtein distance between the old and new text is at most 0.5
- Regularized levenshtein distance between this edit and the first saved edit in the problem is at most 0.5
- The edit is not a simple trimmed copy of the old text (e.g. `"a " -> "a"`)
- The whole problem has at least one edit, which is synthesizable from the first one (first edit excluded)

## Levenshtein distance thresholds

These thresholds were picked without any deeper analysis, just by looking at the data. More research into this is
definitely needed.

## Program synthesis

For PbE, using program synthesis is state of the art (for example documented
by [Wu et al.](https://arxiv.org/pdf/2307.07965.pdf) ).
For program synthesis we need to split the strings into tokens. For this we use regex tokenizer, splitting on all
whitespaces, special characters and camelCase.
For this tool, we use a simple algorithm for synthesizing programs from examples. We have the following operations:

- `Insert(text, index)` - insert text at index
- `Delete(text, index)` - delete text at index
- `Replace(before, after, index)` - replace before with after at index

And for checking whether a given operation is applicable to a given example, we have the following conditions:

- `OnIndex(index)` - the operation is applicable on the given index
- `PreviousToken(token)` - the operation is applicable on the token before the given token
- `NextToken(token)` - the operation is applicable on the token after the given token
- `ThisToken(token)` - the operation is applicable on the given token

Then we use a simple DFS in the space of all programs to find a program, which generates the output from the input.
We describe an `x` synthesizable by `y` when there exists a program `p` such that `p(x) = y`.

As this framework is only used for filtering and not actual synthesizing, we do not need to take care of
text transformations(lowercase -> UPPERCASE, snake_case -> camelCase), whitespace changes etc.,
as these are out of the scope of this project.

## Python usage

We also provide a python notebook (`main/src/python/main.ipynb`) showing, how to use the output in python. We create simple
features definitions for the generated problems and use these to

- create visualization using t-SNE and
- train a simple classifier for predicting whether a given problem is synthesizable from the first example.

To run the notebook, first extract the dataset (see below) and then run the notebook.

## Config

There are some aforementioned (and other) thresholds, which can be changed in the `src/main/resources/config.json` file
for more pleasant use.

## Dataset

There is
a [dataset](https://github.com/eleniel-mocna/PbExtractor/blob/74705ad0328c32480826bfcdab5652df7ff21945/data/intellij-community.json.tar.gz)
generated from the [intellij-community](https://github.com/JetBrains/intellij-community) provided
with this framework.

You can just extract it and use it for your experiments:

```bash
tar -xzf .\data\intellij-community.json.tar.gz
```

This dataset was generated using the following command:

```bash
./gradlew run -PexecArgs="../intellij-community,intellij-community.json,200000"
```

Because of multithreading, a newly generated dataset may differ from the one provided.

## License

This project is governed by the Apache 2.0 license. See [LICENSE](LICENSE) for details.

Copyright 2023, Samuel Soukup <soukup.sam@gmail.com>