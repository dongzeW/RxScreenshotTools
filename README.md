# RxScreenshotTools
根据android里的ContentObserver特性实现监听屏幕截屏
该代码库借鉴于[Piasy/RxScreenshotDetector](https://github.com/Piasy/RxScreenshotDetector),这个实现效果已经很好看,我是根据自己的业务需求做了特殊处理,如有不对不妥的地方,还请指正,万分感谢!❤️

未做gradle dependency处理,因为这样会引入lib里的gradle下的库,这些库可能未跟随自己项目的当前版本走,这样build完,External Libraries下有一堆不同版本的lib,看着不舒服,这些依赖不同版本的lib,打包apk的时候会存在影响.故只做功能代码集成,lib自己配置即可.