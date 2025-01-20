//package org.eclipse.lmos
//
//import org.eclipse.lmos.arc.agents.conversation.ConversationMessage
//
//interface Step {
//    fun execute(conv: List<ConversationMessage>): StepResult
//}
//
//class StepExecutor {
//
//    private val steps = mutableListOf<Step>()
//    private val stepsMap = mutableMapOf<Step, Int>()
//
//    fun seq(): SeqStepBuilder {
//        return SeqStepBuilder(StepExecutor())
//    }
//
//    fun execute(conv: List<ConversationMessage>): List<ConversationMessage> {
//        return executeSteps(0, Continue(conv)).conv
//    }
//
//    private fun executeSteps(currentIndex: Int, stepResult: StepResult): StepResult {
//        val conv = stepResult.conv
//        if(currentIndex >= steps.size) return End(conv)
//        val currentStep = steps[currentIndex]
//
//            return when (val result = currentStep.execute(conv)) {
//                is Loop -> {
//                    val updatedConv = stepResult.conv + result.conv
//                    executeSteps(stepsMap[result.step] ?: (currentIndex + 1), Continue(updatedConv))
//                }
//                else -> executeSteps(currentIndex+1, result)
//            }
//
//    }
//
//    inner class SeqStepBuilder(private val stepExecutor: StepExecutor) {
//
//        fun step(step: Step): SeqStepBuilder {
//            stepExecutor.steps.add(step)
//            stepsMap[step] = steps.size
//            return this
//        }
//
//        fun end(): StepExecutor {
//            return stepExecutor
//        }
//
//    }
//
//}
//
//sealed class StepResult(val conv: List<ConversationMessage>)
//
//class Continue(conv: List<ConversationMessage>) : StepResult(conv)
//
//class Loop(conv: List<ConversationMessage>, val step: Step) : StepResult(conv)
//
//class End(conv: List<ConversationMessage>) : StepResult(conv)
