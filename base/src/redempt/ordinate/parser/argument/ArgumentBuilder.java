package redempt.ordinate.parser.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.constraint.Constraint;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;

import java.util.ArrayList;
import java.util.List;

public class ArgumentBuilder<T, V> {

	private String name;
	private boolean optional;
	private boolean vararg;
	private boolean consuming;
	private ArgType<T, V> type;
	private Constraint<T, ?> constraint;
	private ContextProvider<T, V> defaultValue;

	public void setConsuming(boolean consuming) {
		this.consuming = consuming;
	}

	public void setVararg(boolean vararg) {
		this.vararg = vararg;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(ArgType<T, ?> type) {
		this.type = (ArgType<T, V>) type;
	}

	public void setConstraint(Constraint<T, ?> constraint) {
		this.constraint = constraint;
	}

	public void setDefaultValue(ContextProvider<T, ?> defaultValue) {
		this.defaultValue = (ContextProvider<T, V>) defaultValue;
	}

	public List<CommandComponent<T>> build(ComponentFactory<T> factory) {
		if (vararg && consuming) {
			throw new IllegalStateException("Argument cannot be consuming and vararg");
		}
		List<CommandComponent<T>> list = new ArrayList<>();
		if (consuming) {
			list.add(factory.createConsumingArgument(type, optional, defaultValue, name));
		} else if (vararg) {
			list.add(factory.createVariableLengthArgument(type, optional, name));
			if (constraint != null) {
				constraint = Constraint.listConstraint(constraint);
			}
		} else if (optional) {
			list.add(factory.createOptionalArgument(type, defaultValue, name));
		} else {
			list.add(factory.createArgument(type, name));
		}
		if (constraint != null) {
			CommandComponent<T> component = list.get(0);
			list.add(factory.createConstraint(constraint, component::getIndex, name));
		}
		return list;
	}

}
