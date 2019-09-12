package org.flhy.ext.trans.steps;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.flhy.ext.core.PropsUI;
import org.flhy.ext.trans.step.AbstractStep;
import org.flhy.ext.utils.JSONArray;
import org.flhy.ext.utils.JSONObject;
import org.flhy.ext.utils.StringEscapeHelper;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.dbproc.DBProcMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectMetadataChange;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.pentaho.di.trans.steps.sql.ExecSQLMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

// author sf 2019-08-06
@Component("DBProc")
@Scope("prototype")
public class DBProc extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		DBProcMeta DBProcsMeta = (DBProcMeta) stepMetaInterface;


		String con = cell.getAttribute("connection");
		DBProcsMeta.setDatabase(DatabaseMeta.findDatabase(databases, con));
		DBProcsMeta.setProcedure(cell.getAttribute("procedure"));
		DBProcsMeta.setResultName(cell.getAttribute("resultName"));
		DBProcsMeta.setAutoCommit("Y".equalsIgnoreCase(cell.getAttribute("autoCommit")));
		DBProcsMeta.setResultType(ValueMetaFactory.getIdForValueMeta(cell.getAttribute("resultType")));


//		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute( "argument" ));
//		DBProcsMeta.allocate( jsonArray.size() );
//		for(int i=0; i<jsonArray.size(); i++) {
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			DBProcsMeta.getArgument()[i] = jsonObject.optString("name");
//		}
//
//		JSONArray jsonDirectionArray = JSONArray.fromObject(cell.getAttribute( "argumentDirection" ));
//		DBProcsMeta.allocate( jsonDirectionArray.size() );
//		for(int i=0; i<jsonDirectionArray.size(); i++) {
//			JSONObject jsonObject = jsonDirectionArray.getJSONObject(i);
//			DBProcsMeta.getArgumentDirection()[i] = jsonObject.optString("directionname");
//		}
//
//		JSONArray jsonTypeArray = JSONArray.fromObject(cell.getAttribute( "argumentType" ));
//		DBProcsMeta.allocate( jsonTypeArray.size() );
//		for(int i=0; i<jsonTypeArray.size(); i++) {
//			JSONObject jsonObject = jsonTypeArray.getJSONObject(i);
//			DBProcsMeta.getArgumentType()[i] = jsonObject.optInt("typeName");
//		}


		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("parameterFields"));

		String[] argument = new String[jsonArray.size()];
		String[] argumentDirection = new String[jsonArray.size()];
		int[] argumentType = new int[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			argument[i] = jsonObject.optString("name");
			argumentDirection[i] = jsonObject.optString("direction");


			argumentType[i] = ValueMetaFactory.getIdForValueMeta(jsonObject.optString("type"));

		}

		DBProcsMeta.setArgument(argument);
		DBProcsMeta.setArgumentDirection(argumentDirection);
		DBProcsMeta.setArgumentType(argumentType);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {

		DBProcMeta  DBPMeta = (DBProcMeta) stepMetaInterface;

		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("connection", DBPMeta.getDatabase() == null ? "" : DBPMeta.getDatabase().getName());
		e.setAttribute("procedure", DBPMeta.getProcedure());
		e.setAttribute("resultName", DBPMeta.getResultName());
	//	jsonObject.put("type",ValueMetaFactory.getValueMetaName(DBPMeta.getArgumentType()[j]) );

		e.setAttribute("resultType",ValueMetaFactory.getValueMetaName(Integer.valueOf(DBPMeta.getResultType())));
		e.setAttribute("autoCommit",DBPMeta.isAutoCommit() ? "Y" : "N");


//		JSONArray arguments = new JSONArray();
//		for ( int i = 0; i < DBPMeta.getArgument().length; i++ ) {
//			String name = DBPMeta.getArgument()[i];
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("name", name);
//			arguments.add(jsonObject);
//		}
//		e.setAttribute("arguments", arguments.toString());
//
//		JSONArray directions = new JSONArray();
//		for ( int i = 0; i < DBPMeta.getArgumentDirection().length; i++ ) {
//			String directionname = DBPMeta.getArgumentDirection()[i];
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("directionname", directionname);
//			arguments.add(jsonObject);
//		}
//		e.setAttribute("argumentDirection", directions.toString());
//
//		JSONArray argumentTypes = new JSONArray();
//		for ( int i = 0; i < DBPMeta.getArgumentType().length; i++ ) {
//			String typeName = String.valueOf(DBPMeta.getArgumentType()[i]);
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("typeName", typeName);
//			arguments.add(jsonObject);
//		}
//		e.setAttribute("argumentType", argumentTypes.toString());
		JSONArray jsonArray = new JSONArray();
		String[] parameters = DBPMeta.getArgument();
		for(int j=0; j<parameters.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", parameters[j]);
			jsonObject.put("direction", DBPMeta.getArgumentDirection()[j]);

			jsonObject.put("type",ValueMetaFactory.getValueMetaName(DBPMeta.getArgumentType()[j]) );

			jsonArray.add(jsonObject);
		}
		e.setAttribute("parameterFields", jsonArray.toString());
		return e;
		

	}

}
