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
            <el-input :placeholder="listQuery.sku.title" v-model="listQuery.sku.value" style="width: 450px;" class="filter-item" @keyup.enter.native="search($event)"/>
            <el-select multiple collapse-tags v-model="listQuery.saleStatusList.value" :placeholder="listQuery.saleStatusList.title" clearable style="width: 200px" class="filter-item">
              <el-option v-for="item in queryParam.saleStatusList" :key="item.pSaleId" :label="item.pSaleName" :value="item.pSaleId"/>
            </el-select>
            <el-select v-model="listQuery.categoryList.value" :placeholder="listQuery.categoryList.title" clearable class="filter-item" style="width: 180px">
              <el-option v-for="item in queryParam.categoryList" :key="item.pcId" :label="item.pcName" :value="item.pcId + '-' +item.pcLevel"/>
            </el-select>
            <!-- <el-select v-model="listQuery.sort" style="width: 140px" class="filter-item" @change="handleFilter">
              <el-option v-for="item in sortOptions" :key="item.key" :label="item.label" :value="item.key"/>
            </el-select> -->
            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="search($event)">搜索</el-button>
            <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">新增</el-button> -->
            <el-button class="filter-item" type="primary" icon="el-icon-download" @click="handlExprotDialog()">导出SKU</el-button>
            <!-- <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">测试</el-checkbox> -->
            <el-button v-if="checkPermission(['admin'])" class="filter-item" type="primary" icon="el-icon-refresh" @click="syncProductList()" :loading="syncProductLoading">同步产品</el-button>
            <el-button v-if="checkPermission(['admin'])" class="filter-item" type="primary" icon="el-icon-refresh" @click="parseProductLocalImage()" :loading="parseLocalImageLoading">解析产品图片</el-button>
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
              label="图片" width="100">
              <template slot-scope="scope">
                <el-popover
                  placement="right"
                  title=""
                  trigger="hover">
                  <img @click="handleSetPic(scope.$index, scope.row)" slot="reference" v-lazy="scope.row.pictureUrl" :key="scope.row.pictureUrl" alt="设置SKU主图" style="max-height: 50px;cursor:pointer;">
                  <img v-lazy="scope.row.pictureUrl" style="max-height: 300px;"/>
                </el-popover>
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
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="default"
                  icon="edit"
                  @click="handleSetPicPath(scope.$index, scope.row)">设置图片路径
                </el-button>
                <!-- <el-button
                  size="small"
                  type="default"
                  icon="edit"
                  @click="handleEdit(scope.$index, scope.row)">编辑
                </el-button> -->
                <!-- <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.$index, scope.row)">删除
                </el-button> -->
              </template>
            </el-table-column>
          </el-table>
    
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="tableData.pagination.pageNo"
            :page-sizes="[5, 10, 20, 50, 100, 200]"
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
      <el-dialog title="导出产品信息" width="50%" :visible.sync="exportDialogVisible">
        <div>
          <el-form ref="exportFromData" :model="exportFromData" :rules="formRules" :label-position="exportFromData.position">
            <el-form-item label="输入需要导入的SKU(多个换行)">
              <el-input
                type="textarea"
                :rows="7"
                placeholder="请输入SKU"
                v-model="exportFromData.skuText">
              </el-input>
            </el-form-item>
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
          <el-button type="primary" @click="handlExprot" :loading="exportLoading">导 出</el-button>
        </div>
      </el-dialog>
      <el-dialog title="设置主图" :visible.sync="picDialog" width="50%">
        <div style="height:450px;overflow:auto;">
          <el-form ref="picFormData" :model="picFormData" :rules="formRules" label-width="120px">
            <el-row>
              <el-col :span="8" v-for="(item, index) in picDialogData" :key="index">
                <el-card :body-style="{ padding: '0px' }">
                  <img height="200" :src="item.pictureUrl" class="image" :for="item.id">
                  <div style="padding: 14px;">
                    <div class="bottom clearfix">
                      <el-radio v-model="picFormData.picId" :label="item.id" :id="item.id"></el-radio>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button type="success" @click="picDataDown">下 载</el-button>
          <el-button @click="picDialog = false">取 消</el-button>
          <el-button type="primary" @click="picDataSave" :loading="picSaveLoading">保 存</el-button>
        </div>
      </el-dialog>
      <el-dialog title="设置图片路径" :visible.sync="setPicPathVisible">
        <div>
          <el-form ref="picPathFormData" :model="picPathFormData" :rules="formRules" label-width="120px">
            <el-form-item label="图片路径">
              <el-input name="sku" v-model="picPathFormData.path"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="setPicPathVisible = false">取 消</el-button>
          <el-button type="primary" @click="savePicPath" :loading="picPathSaveLoading">保 存</el-button>
        </div>
      </el-dialog>
  </div>
</template>

<script>
import panel from '@/components/Panel'
import { getList, deleted, update, add, downLoadMix, getSaleStatusList, getCategoryList, getProductPic, saveProductPic, syncProductList, parseProductLocalImage, savePicPath } from '@/api/product'
import checkPermission from '@/utils/permission' // 权限判断函数

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
      picDialog: false,
      formVisible: false,
      saveLoading: false,
      picSaveLoading: false,
      listLoading: false,
      exportDialogVisible: false,
      exportLoading: false,
      syncProductLoading: false,
      parseLocalImageLoading: false,
      setPicPathVisible: false,
      picPathSaveLoading: false,
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
          value: []
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
          pageSize: 20,
          parentId: 0
        },
        rows: []
      },
      selectRows: [],
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
      picFormData: {
        productId: 0,
        picId: 0
      },
      picPathFormData: {
        productId: 0,
        path: ''
      },
      picDialogData: [],
      exportFromData: {
        position: 'top',
        checkAll: false,
        isIndeterminate: true,
        skuText: '',
        fields: [
          { field: 'pictureUrl', checked: true, label: '图片地址' },
          { field: 'pictureData', checked: false, label: '图片' },
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
    checkPermission,
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
      this.selectRows = val
    },
    handleSizeChange(val) {
      this.tableData.pagination.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.tableData.pagination.pageNo = val
      this.loadData()
    },
    handlExprotDialog() {
      this.exportDialogVisible = true
      const trimSkuText = this.exportFromData.skuText.trim()
      let skuList = []
      if (trimSkuText !== '') {
        skuList = trimSkuText.split('\n')
      }
      if (this.selectRows.length > 0) {
        this.selectRows.forEach(element => {
          if (skuList.indexOf(element.productSku) === -1) {
            skuList.push(element.productSku)
          }
        })
      }
      this.exportFromData.skuText = skuList.join('\n')
    },
    handlExprot() {
      const trimSkuText = this.exportFromData.skuText.trim()
      if (trimSkuText === '') {
        this.$message.error('请输入需要导出的SKU列表')
        return
      }
      const skuList = trimSkuText.split('\n')
      if (skuList.length <= 0) {
        this.$message.error('请输入需要导出的SKU列表')
        return
      }
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
      const parameterStr = JSON.stringify({ sku: skuList })
      this.exportLoading = true
      downLoadMix({ fileName: 'exprotSkuList', list: listStr, async: 'false', fileType: fileType, parameter: parameterStr }).then(res => {
        this.$message('下载成功')
        this.exportFromData.skuText = ''
        this.exportLoading = false
      }, reject => {
        this.exportLoading = false
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
    handleSetPicPath(index, row) {
      this.setPicPathVisible = true
      this.picPathFormData.productId = row.id
    },
    savePicPath() {
      console.log(this.picPathFormData)
      if (this.picPathFormData.path === '') {
        return false
      }
      this.picPathSaveLoading = true
      savePicPath(this.picPathFormData).then(res => {
        this.picPathSaveLoading = false
        this.setPicPathVisible = false
        this.$message(res.message)
        this.loadData()
      }).catch(error => {
        this.picPathSaveLoading = false
        this.$message.error(error)
      })
    },
    handleSetPic(index, row) {
      getProductPic({ productId: row.id }).then(res => {
        this.picDialog = true
        this.picFormData.picId = row.mainPictureId
        this.picFormData.productId = row.id
        this.picDialogData = res.data
      }).catch(error => {
        this.$message.error(error)
      })
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
    picDataSave() {
      this.picSaveLoading = true
      saveProductPic(this.picFormData).then(res => {
        this.$message(res.message)
        this.picSaveLoading = false
        this.picDialog = false
        this.loadData()
      }).catch(error => {
        this.picSaveLoading = false
        this.$message.error(error)
      })
    },
    picDataDown() {
      this.$message('开发中...')
      // this.picSaveLoading = true
      // saveProductPic(this.picFormData).then(res => {
      //   this.$message(res.message)
      //   this.picSaveLoading = false
      //   this.picDialog = false
      //   this.loadData()
      // }).catch(error => {
      //   this.picSaveLoading = false
      //   this.$message.error(error)
      // })
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
        params.skuStr = this.listQuery.sku.value
      }
      if (this.listQuery.categoryList.value !== '') {
        var cateArr = this.listQuery.categoryList.value.split('-')
        if (cateArr[1] <= 0) {
          params.category1 = cateArr[0]
        } else {
          params.category2 = cateArr[0]
        }
      }
      if (this.listQuery.saleStatusList.value !== '' && this.listQuery.saleStatusList.value.length > 0) {
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
    },
    syncProductList() {
      this.syncProductLoading = true
      syncProductList().then(res => {
        this.$message(res.message)
        this.syncProductLoading = false
      }).catch(error => {
        this.syncProductLoading = false
        this.$message.error(error)
      })
    },
    parseProductLocalImage() {
      this.parseLocalImageLoading = true
      parseProductLocalImage().then(res => {
        this.$message(res.message)
        this.parseLocalImageLoading = false
      }).catch(error => {
        this.parseLocalImageLoading = false
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
  