
## プロジェクト作成

```shell script
mn create-app micronaut-book --lang kotlin --build gradle --features data-hibernate-jpa,swagger-kotlin
```

## IntelliJ IDEA設定

- Annotation ProcessorをEnableにしておく
- Project SDKは11に設定する
- Cloud Code Pluginを入れる
- Tools => Cloud Code => Kubernetes => add Kubernetes Support

## テスト動作環境構築

- Docker for Macやminikubeなどローカルで動作するKubernetes環境を用意する