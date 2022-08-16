package redempt.ordinate.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import redempt.ordinate.command.Command;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.argument.ArgumentComponent;
import redempt.ordinate.component.argument.ConsumingArgumentComponent;
import redempt.ordinate.component.argument.OptionalArgumentComponent;
import redempt.ordinate.component.argument.VariableLengthArgumentComponent;
import redempt.ordinate.component.flag.BooleanFlagComponent;
import redempt.ordinate.component.flag.FlagComponent;

import java.util.*;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class BrigadierCommandConverter<T, S> {

	private Map<Class<? extends CommandComponent<T>>, BrigadierAdapter<T, ?>> converters = new HashMap<>();
	
	public BrigadierCommandConverter() {
		register(ArgumentComponent.class, (component, builder) ->
				builder.addArgument(argument(component.getName(), getType(component.getType().getName()))));
		register(OptionalArgumentComponent.class, (component, builder) ->
				builder.addArgument(argument(component.getName(), getType(component.getType().getName())), true));
		register(BooleanFlagComponent.class, (component, builder) -> {
			Set<String> names = component.getNames();
			for (String name : names) {
				builder.addFlag(argument(name, string()));
			}
		});
		register(FlagComponent.class, (component, builder) -> {
			Set<String> names = component.getNames();
			String typeName = component.getType().getName();
			ArgumentType<?> type = getType(typeName);
			for (String name : names) {
				builder.addFlag(new BrigadierFlag<>(argument(name, string()), argument(typeName, type)));
			}
		});
		register(VariableLengthArgumentComponent.class, (component, builder) -> builder.addArgument(argument(component.getName(), greedyString()), component.isOptional()));
		register(ConsumingArgumentComponent.class, (component, builder) -> builder.addArgument(argument(component.getName(), greedyString())));
	}
	
	private Set<CommandNode<S>> getTails(CommandNode<S> node) {
		Set<CommandNode<S>> tails = new HashSet<>();
		Set<CommandNode<S>> seen = new HashSet<>();
		Queue<CommandNode<S>> queue = new ArrayDeque<>();
		queue.add(node);
		while (!queue.isEmpty()) {
			node = queue.poll();
			if (!seen.add(node)) {
				continue;
			}
			queue.addAll(node.getChildren());
			if (node.getCommand() != null) {
				tails.add(node);
			}
		}
		return tails;
	}
	
	public <C extends CommandComponent<T>> void register(Class<C> type, BrigadierAdapter<T, C> adapter) {
		converters.put(type, adapter);
	}
	
	public CommandNode<S> convertToBrigadier(CommandBase<T> command) {
		RootCommandNode<S> root = new RootCommandNode<>();
		for (Command<T> child : command.getCommands()) {
			addToNode(child, root);
		}
		return root;
	}
	
	private void addToNode(Command<T> command, CommandNode<S> root) {
		LiteralArgumentBuilder<S> node = literal(command.getName());
		BrigadierBuilder<S> builder = new BrigadierBuilder<>(node);
		for (CommandComponent<T> component : command.getPipeline().getComponents()) {
			convert(component, builder);
		}
		builder.build();
		root.getChildren().add(node.build());
		for (String name : command.getNames()) {
			if (name.equals(command.getName())) {
				continue;
			}
			LiteralArgumentBuilder<S> newNodeBuilder = literal(name);
			CommandNode<S> newNode = newNodeBuilder.build();
			newNode.getChildren().addAll(node.getArguments());
			root.getChildren().add(newNode);
		}
	}
	
	private <C extends CommandComponent<T>> void convert(C component, BrigadierBuilder<S> builder) {
		BrigadierAdapter<T, C> adapter = (BrigadierAdapter<T, C>) converters.get(component.getClass());
		if (adapter == null) {
			return;
		}
		adapter.convert(component, builder);
	}
	
	private ArgumentType<?> getType(String name) {
		switch (name) {
			case "int":
				return integer();
			case "float":
				return floatArg();
			case "double":
				return doubleArg();
			case "long":
				return longArg();
			case "boolean":
				return bool();
			default:
				return string();
		}
	}

}
