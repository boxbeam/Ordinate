package redempt.ordinate.brigadier;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import java.util.*;
import java.util.stream.Collectors;

public class BrigadierBuilder<C> {
	
	private List<ArgumentBuilder<C, ?>> arguments = new ArrayList<>();
	private Set<Integer> optionals = new HashSet<>();
	private List<BrigadierFlag<C>> flags = new ArrayList<>();
	private ArgumentBuilder<C, ?> node;
	
	public BrigadierBuilder(LiteralArgumentBuilder<C> node) {
		this.node = node;
	}
	
	public BrigadierBuilder<C> addArgument(ArgumentBuilder<C, ?> node, boolean optional) {
		if (optional) {
			optionals.add(arguments.size());
		}
		arguments.add(node);
		return this;
	}
	
	public BrigadierBuilder<C> addArgument(ArgumentBuilder<C, ?> node) {
		return addArgument(node, false);
	}
	
	public BrigadierBuilder<C> addFlag(ArgumentBuilder<C, ?> flag) {
		return addFlag(new BrigadierFlag<>(flag));
	}
	
	public BrigadierBuilder<C> addFlag(BrigadierFlag<C> node) {
		flags.add(node);
		return this;
	}
	
	private List<CommandNode<C>> buildAll() {
		if (arguments.size() == 0) {
			node.executes(c -> 0);
			return Collections.emptyList();
		}
		arguments.get(arguments.size() - 1).executes(c -> 0);
		return arguments.stream().map(ArgumentBuilder::build).collect(Collectors.toList());
	}
	
	private void link(List<CommandNode<C>> nodes) {
		if (optionals.contains(arguments.size() - 1)) {
			arguments.get(arguments.size() - 1).executes(c -> 0);
		}
		for (int i = 0; i < arguments.size() - 1; i++) {
			nodes.get(i).addChild(nodes.get(i + 1));
		}
	}
	
	private void handleOptionals(List<CommandNode<C>> nodes) {
		for (int index : optionals) {
			if (index == 0) {
				firstOptional(nodes);
				continue;
			}
			if (index == arguments.size() - 1) {
				continue;
			}
			nodes.get(index - 1).addChild(nodes.get(index + 1));
		}
	}
	
	private boolean[] optionalMap() {
		boolean[] arr = new boolean[arguments.size()];
		for (int i = arguments.size() - 1; i >= 0; i--) {
			if (!optionals.contains(i)) {
				return arr;
			}
			arr[i] = true;
		}
		return arr;
	}
	
	private void handleFlags(List<CommandNode<C>> nodes) {
		boolean[] optionalMap = optionalMap();
		for (BrigadierFlag<C> flag : flags) {
			handleFlag(nodes, flag, optionalMap);
		}
	}
	
	private void handleFlag(List<CommandNode<C>> nodes, BrigadierFlag<C> flag, boolean[] optionalMap) {
		CommandNode<C> first = flag.build(arguments.size() == optionals.size());
		if (nodes.size() > 0) {
			addChild(first, nodes.get(0));
			nodes.get(nodes.size() - 1).addChild(flag.build(true));
		}
		node.then(first);
		
		for (int j = 0; optionals.contains(j) && j < nodes.size() - 1; j++) {
			CommandNode<C> clone = flag.build(optionalMap[j]);
			addChild(clone, nodes.get(j + 1));
			node.then(clone);
		}
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			CommandNode<C> clone = flag.build(optionalMap[i]);
			addChild(clone, nodes.get(i + 1));
			nodes.get(i).addChild(clone);
			
			for (int j = i + 1; optionals.contains(j) && j < nodes.size() - 1; j++) {
				clone = flag.build(optionalMap[j]);
				addChild(clone, nodes.get(j + 1));
				node.then(clone);
			}
		}
	}
	
	private void addChild(CommandNode<C> flag, CommandNode<C> next) {
		if (!flag.getChildren().isEmpty()) {
			flag = flag.getChildren().iterator().next();
		}
		flag.addChild(next);
	}
	
	private void firstOptional(List<CommandNode<C>> nodes) {
		if (arguments.size() == optionals.size()) {
			node.executes(c -> 0);
			return;
		}
		node.then(nodes.get(1));
	}
	
	public void build() {
		List<CommandNode<C>> built = buildAll();
		link(built);
		handleOptionals(built);
		handleFlags(built);
		if (built.size() > 0) {
			node.then(built.get(0));
		}
	}
	
}
