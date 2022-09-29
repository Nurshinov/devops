def call(String APP_NAME) {
    APP_VERSION = env.BUILD_NUMBER
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                        println git_checkout
                    }
                    stage('Собираем docker образ') {
                        sh "docker build -t  ${APP_NAME}:${APP_VERSION} ."
                    }
                    stage('Пушим образ и запускаем') {
                        sh "docker tag ${APP_NAME}:${APP_VERSION} docker.registry.ext:3000/${APP_NAME}:${APP_VERSION}"
                    }
                } catch(exception){
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                    cleanWs()
                }
            }
        }
    }
}
