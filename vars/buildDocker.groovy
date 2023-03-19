def call(String appName) {
    DOCKER_REGISTRY_REPO = alimzhannurshinov
    node {
        ansiColor('xterm') {
            timestamps {
                try {
                    stage('Checkout') {
                        println "Выкачиваем репозиторий с исходным кодом"
                        git_checkout = checkout scm
                    }
                    stage('Build') {
                        println "Собираем образ и пушим в registry"
                        sh "docker build -t ${appName} ."
                        sh "docker push ${DOCKER_REGISTRY_REPO}/${appName.toLowerCase()}"
                    }
                    cleanWs()
                } catch (exception) {
                    println "Сборка упала: " + "\n${exception}"
                    currentBuild.result = 'FAILURE'
                } finally {
                    cleanWs()
                }
            }
        }
    }
}