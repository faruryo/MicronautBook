
## プロジェクト作成

```shell script
mn create-app micronaut-book --lang kotlin --build gradle --features data-hibernate-jpa,swagger-kotlin
```

## IntelliJ IDEA設定

- Annotation ProcessorをEnableにしておく
- Project SDKは11に設定する
- Cloud Code Pluginを入れる
- Tools => Cloud Code => Kubernetes => add Kubernetes Support

## ローカルテスト動作環境構築

- Docker for Macやminikubeなどローカルで動作するKubernetes環境を用意する

## ローカルテスト

- IDEA のRun構成で「micronaut-book [test]」を選ぶとgradle testによりテストが実行される
- IDEA のRun構成で「Develop on Kubernetes」を選ぶとskaffoldによりローカルKubernetes環境にデプロイされる。プログラムに変更を加えるたびに自動的にデプロイされるため、継続的に動作確認が可能になる。