# 開発wiki的なもの


## 開発手順


1. 該当するissueを探す (なければ、プロジェクトオーナーに作らせる)
1. スプリントバックログの内容でブランチ切る
1. 開発する。コミットはこまめに。
1. 完成したら、marge Requestを作る。
  1. merge Requestのtarget branchはdevelop
  1. asignneeを仲嶋、reviewerを澤木
1. 確認次第mergeします


## ブランチの命名規則

基本的には`説明_#(issue番号)`

例.
```
make-event-entity_#1
```


## 必要なhtml(イベント作成時点)

- 基本レイアウト画面
- ヘッダーのテンプレート
- イベント作成画面
- イベント一覧画面



## htmlファイル構成

- templates
  - layout
    - layout.html
    - header.html
  - events
    - eventList.html
    - eventCreate.html
  - (各機能毎のフォルダ)
    - (各ページ.html)

## htmlを書く時のルール

- 各機能ごとにtemlatesの下にフォルダを作成する。

- 必要(CSSのデザインなど)に応じて、共通するタブのclass名を統一する。

- formの属性の順番

1. class
2. id
3. name
4. method
5. action
6. object

- input

1. class
2. id
3. name
4. type
6. field
7. value

- inputのボタン

基本 input type="submit" を利用する。
