/*
 * Copyright (c) 2009 University of Tartu
 */
package org.jpmml.manager;

import java.util.*;

import org.dmg.pmml.*;

abstract
public class ModelManager<M extends Model> extends PMMLManager implements Consumer {

	public ModelManager(){
	}

	public ModelManager(PMML pmml){
		super(pmml);
	}

	abstract
	public M getModel();

	/**
	 * Convenience method for adding a field declaration to {@link DataDictionary} and {@link MiningSchema}.
	 *
	 * @see #addDataField(FieldName, String, OpType, DataType)
	 * @see #addMiningField(FieldName, FieldUsageType)
	 */
	public void addField(FieldName name, String displayName, OpType opType, DataType dataType, FieldUsageType fieldUsageType){
		addDataField(name, displayName, opType, dataType);
		addMiningField(name, fieldUsageType);
	}

	public List<FieldName> getActiveFields(){
		return getMiningFields(FieldUsageType.ACTIVE);
	}

	public FieldName getTarget(){
		List<FieldName> fields = getPredictedFields();

		if(fields.size() < 1){
			throw new InvalidFeatureException("No predicted fields", getMiningSchema());
		} else

		if(fields.size() > 1){
			throw new InvalidFeatureException("Too many predicted fields", getMiningSchema());
		}

		return fields.get(0);
	}

	public List<FieldName> getPredictedFields(){
		return getMiningFields(FieldUsageType.PREDICTED);
	}

	public List<FieldName> getMiningFields(FieldUsageType fieldUsageType){
		List<FieldName> result = new ArrayList<FieldName>();

		List<MiningField> miningFields = getMiningSchema().getMiningFields();
		for(MiningField miningField : miningFields){

			if((miningField.getUsageType()).equals(fieldUsageType)){
				result.add(miningField.getName());
			}
		}

		return result;
	}

	public MiningField getMiningField(FieldName name){
		List<MiningField> miningFields = getMiningSchema().getMiningFields();

		return find(miningFields, name);
	}

	public MiningField addMiningField(FieldName name, FieldUsageType usageType){
		MiningField miningField = new MiningField(name);
		miningField.setUsageType(usageType);

		List<MiningField> miningFields = getMiningSchema().getMiningFields();
		miningFields.add(miningField);

		return miningField;
	}

	public List<FieldName> getOutputFields(){
		List<FieldName> result = new ArrayList<FieldName>();

		Output output = getOrCreateOutput();

		List<OutputField> outputFields = output.getOutputFields();
		for(OutputField outputField : outputFields){
			result.add(outputField.getName());
		}

		return result;
	}

	public OutputField getOutputField(FieldName name){
		Output output = getOrCreateOutput();

		List<OutputField> outputFields = output.getOutputFields();

		return find(outputFields, name);
	}

	@Override
	public DerivedField resolve(FieldName name){
		LocalTransformations localTransformations = getOrCreateLocalTransformations();

		List<DerivedField> derivedFields = localTransformations.getDerivedFields();

		DerivedField derivedField = find(derivedFields, name);
		if(derivedField == null){
			derivedField = super.resolve(name);
		}

		return derivedField;
	}

	public MiningSchema getMiningSchema(){
		return getModel().getMiningSchema();
	}

	public LocalTransformations getOrCreateLocalTransformations(){
		M model = getModel();

		LocalTransformations localTransformations = model.getLocalTransformations();
		if(localTransformations == null){
			localTransformations = new LocalTransformations();

			model.setLocalTransformations(localTransformations);
		}

		return localTransformations;
	}

	public Output getOrCreateOutput(){
		M model = getModel();

		Output output = model.getOutput();
		if(output == null){
			output = new Output();

			model.setOutput(output);
		}

		return output;
	}

	static
	protected <E extends Entity> void putEntity(E entity, Map<String, E> map){
		String id = entity.getId();
		if(id == null || map.containsKey(id)){
			throw new InvalidFeatureException(entity);
		}

		map.put(id, entity);
	}

	static
	protected void ensureNull(Object object){

		if(object != null){
			throw new IllegalStateException();
		}
	}

	static
	protected void ensureNotNull(Object object){

		if(object == null){
			throw new IllegalStateException();
		}
	}
}