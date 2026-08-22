package org.joebobilly.appleattack.utils

import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.RegistryBuilderFactory
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.body.ItemDialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.inventory.ItemStack

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class DialogDefinitionMarker

@Suppress("UnstableApiUsage")
@DialogDefinitionMarker
class DialogDefinition(title: Component) {
    @DialogDefinitionMarker
    class MultiActionBuilder {
        private val actions = mutableListOf<ActionButton>()
        private var exitAction: ActionButton? = null
        private var columns = 2

        fun add(action: ActionButton) {
            actions.add(action)
        }
        fun add(label: Component, init: ActionButton.Builder.() -> DialogAction?) {
            add(actionButton(label, init))
        }
        fun exitAction(exitAction: ActionButton) {
            this.exitAction = exitAction
        }
        fun columns(columns: Int) {
            require(columns > 0) { "Columns must be greater than 0" }
            this.columns = columns
        }

        fun build(): DialogType = DialogType.multiAction(actions, exitAction, columns)
    }
    @DialogDefinitionMarker
    class BodyBuilder {
        internal val body = mutableListOf<DialogBody>()
        fun add(dialogBody: DialogBody) {
            body.add(dialogBody)
        }
        fun message(contents: Component, width: Int = 200) {
            add(DialogBody.plainMessage(contents, width))
        }
        fun item(item: ItemStack, init: ItemDialogBody.Builder.() -> Unit) {
            add(DialogBody.item(item).apply(init).build())
        }
    }
    @DialogDefinitionMarker
    class InputsBuilder {
        class SingleOptionBuilder(val key: String, val label: Component) {
            private val entries = mutableListOf<SingleOptionDialogInput.OptionEntry>()
            private var hasInitial = false
            private var width = 200
            private var labelVisible = true

            fun add(key: String, display: Component? = null, initial: Boolean = false) {
                check(!initial || !hasInitial) { "Cannot add two initial options" }
                entries.add(SingleOptionDialogInput.OptionEntry.create(key, display, initial))
                if(initial) hasInitial = true
            }

            fun width(width: Int) {
                require(width in 1..1024) { "Width must be between 1 and 1024" }
                this.width = width
            }
            fun labelVisible(labelVisible: Boolean) {
                this.labelVisible = labelVisible
            }

            fun build(): DialogInput {
                return DialogInput.singleOption(key, width, entries.toList(), label, labelVisible)
            }
        }

        internal val inputs = mutableListOf<DialogInput>()
        fun add(dialogInput: DialogInput) {
            inputs.add(dialogInput)
        }
        fun numberRange(
            key: String, label: Component, start: Float, end: Float,
            init: NumberRangeDialogInput.Builder.() -> Unit
        ) {
            add(DialogInput.numberRange(key, label, start, end).apply(init).build())
        }
        fun singleOption(key: String, label: Component, init: SingleOptionBuilder.() -> Unit) {
            add(SingleOptionBuilder(key, label).apply(init).build())
        }
        fun text(key: String, label: Component, init: TextDialogInput.Builder.() -> Unit) {
            add(DialogInput.text(key, label).apply(init).build())
        }
    }

    private val base = DialogBase.builder(title)
    private val body = BodyBuilder()
    private val inputs = InputsBuilder()
    private var type: DialogType? = null

    fun externalTitle(externalTitle: Component?) {
        base.externalTitle(externalTitle)
    }
    fun canCloseWithEscape(canCloseWithEscape: Boolean = true) {
        base.canCloseWithEscape(canCloseWithEscape)
    }
    fun afterAction(afterAction: DialogBase.DialogAfterAction) {
        base.afterAction(afterAction)
    }
    fun body(init: BodyBuilder.() -> Unit) {
        init(this.body)
    }
    fun inputs(init: InputsBuilder.() -> Unit) {
        init(this.inputs)
    }
    fun addInput(input: DialogInput) {
        this.inputs.add(input)
    }
    fun type(type: DialogType) {
        this.type = type
    }
    fun notice() = type(DialogType.notice())
    fun notice(okButton: ActionButton) = type(DialogType.notice(okButton))
    fun notice(label: Component, init: ActionButton.Builder.() -> DialogAction?) = notice(actionButton(label, init))
    fun confirmation(yesButton: ActionButton, noButton: ActionButton) = type(DialogType.confirmation(yesButton, noButton))
    fun multiAction(init: MultiActionBuilder.() -> Unit) = type(MultiActionBuilder().apply(init).build())

    fun build(it: RegistryBuilderFactory<Dialog, out DialogRegistryEntry.Builder>) {
        base.body(body.body).inputs(inputs.inputs)
        it.empty().base(base.build()).type(type ?: DialogType.notice())
    }
    fun create(): Dialog {
        return Dialog.create { build(it) }
    }
}

fun dialogDefinition(title: Component, init: DialogDefinition.() -> Unit) = DialogDefinition(title).apply(init)
fun dialog(title: Component, init: DialogDefinition.() -> Unit) = dialogDefinition(title, init).create()

@Suppress("UnstableApiUsage")
fun actionButton(label: Component, init: ActionButton.Builder.() -> DialogAction?): ActionButton {
    val builder = ActionButton.builder(label)
    return builder.action(builder.init()).build()
}

@Suppress("UnstableApiUsage")
val ClickEvent<*>.dialog get() = DialogAction.staticAction(this)

@Suppress("UnstableApiUsage")
fun dynamicCallback(callback: DialogActionCallback): DialogAction {
    return DialogAction.customClick(callback, ClickCallback.Options.builder().build())
}