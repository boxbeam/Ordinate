package redempt.ordinate.parser.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.flag.FlagComponent;
import redempt.ordinate.constraint.Constraint;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;

import java.util.ArrayList;
import java.util.List;

public class FlagBuilder<T, V> {
	
	private String[] names;
	private ArgType<T, V> type;
	private Constraint<T, V> constraint;
	private ContextProvider<T, V> defaultValue;
	
	public FlagBuilder(String[] names) {
		this.names = names;
	}
	
	public FlagBuilder<T, V> setType(ArgType<T, ?> type) {
		this.type = (ArgType<T, V>) type;
		return this;
	}
	
	public FlagBuilder<T, V> setConstraint(Constraint<T, ?> constraint) {
		this.constraint = (Constraint<T, V>) constraint;
		return this;
	}
	
	public FlagBuilder<T, V> setDefaultValue(ContextProvider<T, ?> defaultValue) {
		this.defaultValue = (ContextProvider<T, V>) defaultValue;
		return this;
	}
	
	public List<CommandComponent<T>> build(ComponentFactory<T> factory) {
		List<CommandComponent<T>> list = new ArrayList<>();
		FlagComponent<T, V> flag = factory.createFlag(names, type, defaultValue);
		list.add(flag);
		if (constraint != null) {
			list.add(factory.createConstraint(constraint, flag::getIndex, names[0]));
		}
		return list;
	}
	
}
