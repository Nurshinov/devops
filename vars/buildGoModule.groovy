def call() {
    node {
    timestamps {
        stage('Checkout') {
            println "Выкачиваем репозиторий с исходным кодом"
            git_checkout = checkout scm
            println git_checkout
        }
        stage('Build') {
            println "Собираем"
            sh "go build -o online_shop"
        }
        stage('Deploy') {
            println "Заливаем через ансибл"
        }
        cleanWs()
    }
    }
}