<template>
  <div>
    <imp-panel>
        <div class="box-title" slot="header" style="width: 100%;">
          <!-- <el-row style="width: 100%;">
            <el-col :span="12">
              <el-button type="primary" icon="el-icon-plus" @click="exportDialogVisible = true">导出</el-button>
              <router-link :to="{ path: 'userAdd'}">
                
              </router-link> 
            </el-col>
            <el-col :span="12">
              <div class="el-input" style="width: 200px; float: right;">
                <i class="el-input__icon el-icon-search"></i>
                <input type="text" placeholder="sku" v-model="searchKey" @keyup.enter="search($event)"
                        class="el-input__inner">
              </div>
            </el-col>
          </el-row> -->
          <div class="filter-container">
            <el-input :placeholder="listQuery.sku.title" v-model="listQuery.sku.value" style="width: 200px;" class="filter-item" @keyup.enter.native="search($event)"/>
            <el-select v-model="listQuery.saleStatusList.value" :placeholder="listQuery.saleStatusList.title" clearable style="width: 160px" class="filter-item" @change="search($event)">
              <el-option v-for="item in queryParam.saleStatusList" :key="item.pSaleId" :label="item.pSaleName" :value="item.pSaleId"/>
            </el-select>
            <el-select v-model="listQuery.categoryList.value" :placeholder="listQuery.categoryList.title" clearable class="filter-item" style="width: 160px" @change="search($event)">
              <el-option v-for="item in queryParam.categoryList" :key="item.pcId" :label="item.pcName" :value="item.pcId"/>
            </el-select>
            <!-- <el-select v-model="listQuery.sort" style="width: 140px" class="filter-item" @change="handleFilter">
              <el-option v-for="item in sortOptions" :key="item.key" :label="item.label" :value="item.key"/>
            </el-select> -->
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="search($event)">搜索</el-button>
            <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">新增</el-button> -->
            <el-button class="filter-item" type="primary" icon="el-icon-download" @click="exportDialogVisible = true">导出</el-button>
            <!-- <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">测试</el-checkbox> -->
          </div>
        </div>
        <div slot="body" v-loading="listLoading">
          <el-table
            :data="tableData.rows"
            border
            style="width: 100%"
            @selection-change="handleSelectionChange">
            <!--checkbox 适当加宽，否则IE下面有省略号 https://github.com/ElemeFE/element/issues/1563-->
            <el-table-column
              prop="id"
              type="selection"
              width="50">
            </el-table-column>
            <el-table-column
              label="图片" width="76">
              <template slot-scope="scope">
                <img :src='scope.row.pictureUrl' style="height: 35px;vertical-align: middle;" alt="">
              </template>
            </el-table-column>
            <el-table-column
              prop="productSku"
              label="SKU">
            </el-table-column>
            <el-table-column
              label="产品名称">
              <template slot-scope="scope">
                <span>{{ scope.row.productTitle }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="saleStatusText"
              label="销售状态">
            </el-table-column>
            <el-table-column
              prop="productWeight"
              label="重量(KG)">
            </el-table-column>
            <el-table-column
              label="尺寸(CM)">
              <template slot-scope="scope">
                <span>长:{{ scope.row.productLength }}cm 宽:{{ scope.row.productWidth }}cm 高:{{ scope.row.productHeight }}cm</span>
              </template>
            </el-table-column>
            <el-table-column
              label="品类">
              <template slot-scope="scope">
                <span>{{ scope.row.procutCategoryName1 }}/{{ scope.row.procutCategoryName2 }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="defaultSupplierCode"
              label="供应商Code">
            </el-table-column>
            <el-table-column
              label="供应商价格">
              <template slot-scope="scope">
                <span>{{ scope.row.spUnitPrice }}{{ scope.row.spCurrencyCode }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="productAddTime"
              label="产品开发时间">
            </el-table-column>
            <!-- <el-table-column label="操作" width="285">
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="default"
                  icon="edit"
                  @click="handleEdit(scope.$index, scope.row)">编辑
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.$index, scope.row)">删除
                </el-button>
              </template>
            </el-table-column> -->
          </el-table>
    
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="tableData.pagination.pageNo"
            :page-sizes="[5, 10, 20]"
            :page-size="tableData.pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.pagination.total">
          </el-pagination>
        </div>
      </imp-panel>
      <el-dialog title="新增产品" :visible.sync="formVisible">
        <div>
          <el-form ref="formData" :model="formData" :rules="formRules" label-width="120px">
            <el-form-item label="SKU" prop="sku">
              <el-input name="sku" v-model="formData.sku"></el-input>
            </el-form-item>
            <el-form-item label="产品名称" prop="cnName">
              <el-input name="cnName" v-model="formData.cnName"></el-input>
            </el-form-item>
            <el-form-item label="重量" prop="weight">
              <el-input name="weight" type="number" v-model="formData.weight"></el-input>
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input name="price" type="number" v-model="formData.price"></el-input>
            </el-form-item>
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input name="supplierName" v-model="formData.supplierName"></el-input>
            </el-form-item>
            <el-form-item label="供应商产品链接" prop="skuLink">
              <el-input name="skuLink" v-model="formData.skuLink"></el-input>
            </el-form-item>
            <el-form-item label="销量" prop="salesVolume">
              <el-input name="salesVolume" v-model="formData.salesVolume"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="formVisible = false">取 消</el-button>
          <el-button type="primary" @click="addSave" :loading="saveLoading">保 存</el-button>
        </div>
      </el-dialog>
      <el-dialog title="导出产品信息" width="25%" :visible.sync="exportDialogVisible">
        <div>
          <el-form ref="exportFromData" :model="exportFromData" :rules="formRules" :label-position="exportFromData.position">
            <el-form-item label="选择导出字段">
              <el-checkbox v-for="(item) in exportFromData.fields" v-model="item.checked" :label="item.field" :key="item.field">{{ item.label }}</el-checkbox>
            </el-form-item>
            <el-form-item label="文件类型">
              <el-radio-group v-model="exportFromData.fileType">
                <el-radio label="XLS"></el-radio>
                <el-radio label="XLSX"></el-radio>
                <el-radio label="CSV"></el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="exportDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handlExprot" :loading="saveLoading">导 出</el-button>
        </div>
      </el-dialog>
  </div>
</template>

<script>
import panel from '@/components/Panel'
import { getList, deleted, update, add, downLoadMix, getSaleStatusList, getCategoryList } from '@/api/product'

export default {
  components: {
    'imp-panel': panel
  },
  data() {
    /* const validatePass = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能小于6位'))
      } else {
        callback()
      }
    }*/
    return {
      currentRow: {},
      formVisible: false,
      saveLoading: false,
      listLoading: false,
      exportDialogVisible: false,
      defaultProps: {
        children: 'children',
        label: 'name',
        id: 'id'
      },
      queryParam: {
        saleStatusList: [],
        categoryList: []
      },
      listQuery: {
        sku: {
          title: 'SKU',
          value: ''
        },
        saleStatusList: {
          title: '销售状态',
          value: ''
        },
        categoryList: {
          title: '产品分类',
          value: ''
        }
      },
      tableData: {
        pagination: {
          total: 0,
          pageNo: 1,
          pageSize: 10,
          parentId: 0
        },
        rows: []
      },
      formData: {
        id: 0,
        sku: '',
        cnName: '',
        weight: 0,
        price: 0,
        supplierName: '',
        skuLink: '',
        salesVolume: 0
      },
      exportFromData: {
        position: 'top',
        checkAll: false,
        isIndeterminate: true,
        fields: [
          { field: 'productSku', checked: true, label: 'SKU' },
          { field: 'productTitle', checked: true, label: '名称' },
          { field: 'productWeight', checked: true, label: '重量' },
          { field: 'spUnitPrice', checked: true, label: '供应商价格' },
          { field: 'spCurrencyCode', checked: true, label: '供应商价格币种' }
        ],
        fileType: 'XLS'
      },
      formRules: {
        sku: [{ required: true, trigger: 'blur' }],
        cnName: [{ required: true, trigger: 'blur' }]
      }
    }
  },
  methods: {
    addSave() {
      this.$refs.formData.validate(valid => {
        if (valid) {
          this.saveLoading = true
          add(this.formData).then(res => {
            this.saveLoading = false
            this.formVisible = false
            this.$message(res.message)
            this.loadData()
          }).catch(error => {
            this.saveLoading = false
            this.$message.error(error)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    editSave() {
      this.$refs.formData.validate(valid => {
        if (valid) {
          this.saveLoading = true
          update(this.formData).then(res => {
            this.saveLoading = false
            this.formVisible = false
            this.$message(res.message)
            this.loadData()
          }).catch(error => {
            this.$message.error(error)
          })
        } else {
          return false
        }
      })
    },
    search(target) {
      this.loadData()
    },
    handleSelectionChange(val) {
      console.log(val)
    },
    handleSizeChange(val) {
      this.tableData.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.tableData.pagination.pageNo = val
      this.loadData()
    },
    handlExprot() {
      // console.log(this.exportFromData)
      const exportFileList = []
      this.exportFromData.fields.forEach(element => {
        if (element.checked) {
          exportFileList.push({ field: element.field, title: element.label })
        }
      })
      if (exportFileList.length <= 0) {
        this.$message.error('请选择需要导出字段')
        return
      }
      let fileType = 1
      switch (this.exportFromData.fileType) {
        case 'XLS':
          fileType = 1
          break
        case 'XLSX':
          fileType = 2
          break
        case 'CSV':
          fileType = 3
          break
      }
      const listStr = JSON.stringify(exportFileList)
      // { list: listStr, async: 'false', parameter: '{"sku": "A301020101"}' }
      downLoadMix({ list: listStr, async: 'false', fileType: fileType }).then(res => {
        this.$message('下载成功')
      }, reject => {
        this.$message.error(reject)
      })
    },
    handleAdd() {
      this.formData = {
        id: '',
        sku: '',
        cnName: '',
        weight: 0,
        price: 0,
        supplierName: '',
        skuLink: '',
        salesVolume: 0
      }
      this.formVisible = true
    },
    handleEdit(index, row) {
      this.formVisible = true
      this.formData = { ...row }
      // console.log(row)
      // this.$router.push({
      //   path: 'userAdd', query: {
      //     id: row.id
      //   }
      // })
    },
    handleDelete(index, row) {
      deleted({ id: row.id }).then(res => {
        this.$message(res.message)
        this.loadData()
      }).catch(error => {
        this.$message.error(error)
      })
    },
    loadQueryParams() {
      if (this.queryParam.saleStatusList.length <= 0) {
        getSaleStatusList().then(res => {
          this.queryParam.saleStatusList = res.data
        })
      }
      if (this.queryParam.categoryList.length <= 0) {
        getCategoryList().then(res => {
          this.queryParam.categoryList = res.data
        })
      }
    },
    loadData() {
      this.listLoading = true
      const params = {
        pagination: {
          size: this.tableData.pagination.pageSize,
          current: this.tableData.pagination.pageNo
        }
      }
      if (this.listQuery.sku.value !== '') {
        params.sku = [this.listQuery.sku.value]
      }
      if (this.listQuery.saleStatusList.value !== '') {
        params.saleStatus = this.listQuery.saleStatusList.value
      }
      getList(params).then(res => {
        this.listLoading = false
        this.tableData.rows = res.data.list
        this.tableData.pagination.total = res.data.totalCount
        // this.tableData.pagination.pageSize = res.data.totalPage
      }).catch(error => {
        this.listLoading = false
        this.$message.error(error)
      })
    }
  },
  created() {
    this.loadQueryParams()
    this.loadData()
  }
}
</script>
<style>
  .el-pagination {
    float: right;
    margin-top: 15px;
  }
</style>
  