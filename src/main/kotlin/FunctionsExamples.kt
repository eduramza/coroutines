import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/***
 * Anotações
 * runBlocking = ela de fato bloqueia a thread e executa os códigos na sequencia da finalização do anterior, por isso
 * ao rodar os exemplos, o segundo print tem um delay de 1s e por isso o 3° não é printado, apenas depois do termino do delay
 * do segundo, isso é util para garantir que um código só seja executado na sequencia do termino do anterior;
 *
 * GlobalScope.launch = no exemplo ele é chamada, mas como é apresentado um delay dentro dele, o compilador continua executando os
 * códigos seguintes, com isso o 'two' não é printado na tela, ele de fato só executa os 3 comandos quando é adicionado um delay()
 * que garante que aquele método pode ficar rodando por mais alguns segundos, tempo suficiente para execução do código 'two';
 *
 * delay = bloqueia a main thread e garante a sobrevivencia de um certo metodo
 */
fun main(args: Array<String>){
    exampleWithContext()
}

suspend fun printlineDelayed(message : String){
    delay(1000)
    println(message)
}

suspend fun calculate(num: Int): Int{
    delay(1000)
    return num * 10
}

fun exampleBlocking() = runBlocking {
    println("one")
    printlineDelayed("two")
    println("three")
}

//rodar uma outra thread sem bloquar a thread main
fun exampleBlockingDispatcher(){
    runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlineDelayed("two - from thread ${Thread.currentThread().name}")
    }
    //saida do runBlocking que não está rodando e bloquando a main
    println("three - from thread ${Thread.currentThread().name}")

}

fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlineDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    delay(1000) //se remover o delay o código é lido corridamente, ou seja, não é esperado o delay do scope
}

fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlineDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    job.join()
}

fun exampleLaunchCoroutineScope() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    launch(Dispatchers.Default) {
        printlineDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
}

fun customDispatcher() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printlineDelayed("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")

    //customDispatchers devem ser desligadas manualmente
    (customDispatcher.executor as ExecutorService).shutdown()
}

fun exampleAsyncAwait() = runBlocking {
    val startTime = System.currentTimeMillis()

    //nesse caso todas as chamadas estão acontecendo ao mesmo tempo concorrentemente
    val deffered1 = async { calculate(10) }
    val deffered2 = async { calculate(20) }
    val deffered3 = async { calculate(30) }

    //se o await for colocado na criação da chamada acima então cada bloco esperara a linha de cima ser concluida e
    //com isso aumentará o tempo de execução
    val sum = deffered1.await() + deffered2.await() + deffered3.await()

    println("async/await result = $sum")
    val endTime = System.currentTimeMillis()
    println("Time taken = ${endTime - startTime}")
}

fun exampleWithContext() = runBlocking {
    val startTime = System.currentTimeMillis()

    val result1 = withContext(Dispatchers.Default) { calculate(10) }
    val result2 = withContext(Dispatchers.Default) { calculate(20) }
    val result3 = withContext(Dispatchers.Default) { calculate(30) }

    val sum = result1 + result2 + result3

    println("async/await result = $sum")
    val endTime = System.currentTimeMillis()
    println("Time taken = ${endTime - startTime}")
}
