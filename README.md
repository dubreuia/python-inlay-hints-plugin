# Python Inlay Hints Plugin

![Build](https://github.com/dubreuia/python-inlay-hints-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18174-python-inlay-hints.svg)](https://plugins.jetbrains.com/plugin/18174-python-inlay-hints)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18174-python-inlay-hints.svg)](https://plugins.jetbrains.com/plugin/18174-python-inlay-hints)

<!-- Plugin description -->

## Description

Show inferred Python types as Inlay Hints using folds (while we wait for [Displaying inlay hints for inferred types](https://youtrack.jetbrains.com/issue/PY-45743)).

## Features

- Shows inferred type for any variable
- Works on non-recursive generic collection types

## Usage

Showing and hiding types is done using the default folding commands:

- **Show all the types**
    - "Collapse All" (<kbd>ctrl/cmd</kbd> + <kbd>shift</kbd> + <kbd>-</kbd>)
    - "Uncollapse All" (<kbd>ctrl/cmd</kbd> + <kbd>shift</kbd> + <kbd>+</kbd>)
- **Hide all the types**
    - "Uncollapse All" (<kbd>ctrl/cmd</kbd> + <kbd>shift</kbd> + <kbd>+</kbd>)

<!-- Plugin description end -->

## Example

| Python Inlay Hints in folds                                                            | Python Inlay Hints Demo                                                       | 
|----------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| <img src="./docs/screenshot-01.png" alt="python inlay hints screenshot" width="400px"> | <img src="./docs/video-01.gif" alt="python inlay hints video" width="400px"/> |

## TODO

- [ ] [Move to multi-module](https://plugins.jetbrains.com/docs/intellij/plugin-compatibility.html#modules-specific-to-functionality) (intellij has python plugin as well)
- [ ] https://github.com/JetBrains/intellij-sdk-code-samples
