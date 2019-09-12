DBProcDialog = Ext.extend(KettleTabDialog, {
	title: '调用DB存储过程',
	width: 600,
	height: 450,
	initComponent: function() {
		var me = this, cell = getActiveGraph().getGraph().getSelectionCell();

		var wConnection = new Ext.form.ComboBox({
			flex: 1,
			displayField: 'name',
			valueField: 'name',
			typeAhead: true,
			mode: 'remote',
			forceSelection: true,
			triggerAction: 'all',
			selectOnFocus:true,
			store: getActiveGraph().getDatabaseStoreAll(),
			name: 'connection',
			value: cell.getAttribute('connection')
		});

		var onDatabaseCreate = function(dialog) {
			getActiveGraph().onDatabaseMerge(dialog.getValue());
			wConnection.setValue(dialog.getValue().name);
			dialog.close();
		};

		var wprocedure = new Ext.form.TextField({
			//	region: 'center',
			fieldLabel: '存储过程名称',
			anchor: '-10', value: cell.getAttribute('procedure')});

		var wautoCommit = new Ext.form.Checkbox({fieldLabel: '启用自动提交', checked: cell.getAttribute('autoCommit') == 'Y'});
		var wresultName = new Ext.form.TextField({fieldLabel: '返回值名称',  anchor: '-10', value: cell.getAttribute('resultName')});
	//	var wresultType = new Ext.form.TextField({fieldLabel: '返回值类型',  anchor: '-10', value: cell.getAttribute('resultType')});
		var wresultType =	new Ext.form.ComboBox({
	 		store: new Ext.data.JsonStore({
				fields: ['value', 'text'],
				data: [
					{value: 'Number', text: 'Number'},
					{value: 'String', text: 'String'},
					{value: 'Date', text: 'Date'}

				]
			}),
			displayField: 'text',
			valueField: 'value',
			typeAhead: true,
			mode: 'local',
			forceSelection: true,
			triggerAction: 'all',
			selectOnFocus:true,
			value: cell.getAttribute('resultType')
		});




		var store = new Ext.data.JsonStore({
			fields: [ 'name', 'direction', 'type'],
			data: Ext.decode(cell.getAttribute('parameterFields') || Ext.encode([]))
		});




		this.getValues = function(){
			return {

				connection: wConnection.getValue(),
				procedure: wprocedure.getValue(),
				autoCommit: wautoCommit.getValue() ? "Y" : "N",
				resultName: wresultName.getValue(),
				resultType:  wresultType.getValue(),
				parameterFields: Ext.encode(store.toJson())
			};
		};

		this.tabItems = [{
			title: '基本配置',
			xtype: 'KettleForm',
			bodyStyle: 'padding: 10px 0px',
			labelWidth: 150,
			items: [{
				xtype: 'compositefield',
				fieldLabel: '数据库连接',
				anchor: '-10',
				items: [wConnection, {
					xtype: 'button', text: '编辑...', handler: function() {
						var databaseDialog = new DatabaseDialog();
						databaseDialog.on('create', onDatabaseCreate);
						databaseDialog.show(null, function() {
							databaseDialog.initTransDatabase(wConnection.getValue());
						});
					}
				}, {
					xtype: 'button', text: '新建...', handler: function() {
						var databaseDialog = new DatabaseDialog();
						databaseDialog.on('create', onDatabaseCreate);
						databaseDialog.show(null, function() {
							databaseDialog.initTransDatabase(null);
						});
					}
				}, {
					xtype: 'button', text: '向导...'
				}]
			},

				wprocedure,wautoCommit,wresultName,
				{

					xtype: 'compositefield',
					fieldLabel: '数据库连接',
					anchor: '-10',
					items :wresultType

				}

			]
		},
			{
			title: '参数',
			xtype: 'editorgrid',
			tbar: [{
				text: '新增字段', handler: function(btn) {
					var grid = btn.findParentByType('editorgrid');
					var RecordType = grid.getStore().recordType;
					var rec = new RecordType({  name: '', direction: '' ,type:''});
					grid.stopEditing();
					grid.getStore().insert(0, rec);
					grid.startEditing(0, 0);
				}
			},{
				text: '删除字段', handler: function(btn) {
					var sm = btn.findParentByType('editorgrid').getSelectionModel();
					if(sm.hasSelection()) {
						var row = sm.getSelectedCell()[0];
						store.removeAt(row);
					}
				}
			}

			],
			columns: [new Ext.grid.RowNumberer(),
			//	var wresultName = new Ext.form.TextField({fieldLabel: '返回值名称',  anchor: '-10', value: cell.getAttribute('resultName')});

		{
					header: '名称', dataIndex: 'name', width: 100, editor: new Ext.form.TextField({
						// store: new Ext.data.JsonStore({
						// 	fields: ['value', 'text'],
						// 	data: [
						// 		{value: 'INT', text: 'INT'},
						// 		{value: 'OUT', text: 'OUT'},
						// 		{value: 'INTOUT', text: 'INTOUT'},
						//
						// 	]
						// }),
						displayField: 'text',
						valueField: 'value',
						typeAhead: true,
						mode: 'local',
						forceSelection: true,
						triggerAction: 'all',
						selectOnFocus:true
					})
				},
				{
					header: '方向', dataIndex: 'direction', width: 100, editor: new Ext.form.ComboBox({
						store: new Ext.data.JsonStore({
							fields: ['value', 'text'],
							data: [
								{value: 'IN', text: 'IN'},
								{value: 'OUT', text: 'OUT'},
								{value: 'INOUT', text: 'INOUT'},

								]
						}),
						displayField: 'text',
						valueField: 'value',
						typeAhead: true,
						mode: 'local',
						forceSelection: true,
						triggerAction: 'all',
						selectOnFocus:true
					})
				},
				{
					header: '类型', dataIndex: 'type', width: 100, editor: new Ext.form.ComboBox({
						store: new Ext.data.JsonStore({
							fields: ['value', 'text'],
							data: [
								{value: 'Number', text: 'Number'},
								{value: 'String', text: 'String'},
								{value: 'Date', text: 'Date'},

							]
						}),
						displayField: 'text',
						valueField: 'value',
						typeAhead: true,
						mode: 'local',
						forceSelection: true,
						triggerAction: 'all',
						selectOnFocus:true
					})
				},

			],
			store: store
		}];

		DBProcDialog.superclass.initComponent.call(this);
	}



});

Ext.reg('DBProc', DBProcDialog);